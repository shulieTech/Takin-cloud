package io.shulie.takin.cloud.biz.service.scene.impl;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.PlatformTransactionManager;

import io.shulie.takin.ext.content.script.ScriptNode;
import io.shulie.takin.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.ext.content.enginecall.PtConfigExt;
import io.shulie.takin.cloud.biz.service.scene.SceneService;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneSlaRefEntity;
import io.shulie.takin.cloud.data.mapper.mysql.SceneManageMapper;
import io.shulie.takin.cloud.data.mapper.mysql.SceneSlaRefMapper;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.ext.content.enginecall.ThreadGroupConfigExt;
import io.shulie.takin.cloud.open.request.scene.manage.SceneRequest;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.biz.service.scene.SceneSynchronizeService;
import io.shulie.takin.cloud.open.request.scene.manage.SynchronizeRequest;
import io.shulie.takin.cloud.data.model.mysql.SceneBusinessActivityRefEntity;
import io.shulie.takin.cloud.data.mapper.mysql.SceneBusinessActivityRefMapper;

/**
 * 场景同步服务 - 新 - 实现
 *
 * @author 张天赐
 */
@Slf4j
public class SceneSynchronizeServiceImpl implements SceneSynchronizeService {
    @Resource
    SceneService sceneService;
    @Resource
    SceneManageMapper sceneManageMapper;
    @Resource
    SceneSlaRefMapper sceneSlaRefMapper;
    @Resource
    SceneBusinessActivityRefMapper sceneBusinessActivityRefMapper;

    // 事务控制

    private final ThreadLocal<String> transactionIdentifier = new ThreadLocal<>();
    @Resource
    private TransactionDefinition transactionDefinition;
    @Resource
    private PlatformTransactionManager platformTransactionManager;

    /**
     * 同步
     *
     * @param request 入参信息
     *                <ul>
     *                <li>脚本主键</li>
     *                <li>脚本解析结果</li>
     *                <li>脚本节点和业务活动对应关系</li>
     *                </ul>
     * @return 同步事务标识
     */
    @Override
    public String synchronize(SynchronizeRequest request) {
        // 事务标识
        transactionIdentifier.set(UUID.randomUUID().toString());
        // 脚本主键
        long scriptId = request.getScriptId();
        // 脚本解析结果
        final List<ScriptNode> analysisResult = request.getAnalysisResult();
        // 脚本节点和业务活动对应关系
        final Map<String, Long> businessActivityRef = request.getBusinessActivityRef();
        // 脚本解析结果分组
        final Map<NodeTypeEnum, List<ScriptNode>> analysisGroupResult = new HashMap<>(4);
        groupByAnalysisResult(analysisResult, analysisGroupResult);
        // 同步模块
        {
            // 采样器节点MD5
            final Set<String> samplerMd5 = analysisGroupResult.get(NodeTypeEnum.SAMPLER).stream()
                .map(ScriptNode::getXpathMd5).collect(Collectors.toSet());
            // 控制器节点MD5
            final Set<String> controllerMd5 = analysisGroupResult.get(NodeTypeEnum.CONTROLLER).stream()
                .map(ScriptNode::getXpathMd5).collect(Collectors.toSet());
            // 线程组节点MD5
            final Set<String> threadGroupMd5 = analysisGroupResult.get(NodeTypeEnum.THREAD_GROUP).stream()
                .map(ScriptNode::getXpathMd5).collect(Collectors.toSet());
            // 校验参数是否合法
            {
                // 关联关系的所有Key       - Copy
                Set<String> businessActivityKey = new HashSet<>(businessActivityRef.keySet());
                // 采样器的所有xPath MD5  - Copy
                Set<String> tempSamplerMd5 = new HashSet<>(samplerMd5);
                // 合并
                tempSamplerMd5.addAll(businessActivityKey);
                // 移除
                tempSamplerMd5.removeAll(businessActivityKey);
                // 如果还有值，则代表未传入全部的参数
                if (tempSamplerMd5.size() != 0) {
                    throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_UPDATE_ERROR, "采样器与业务活动关联关系不匹配");
                }
            }
            // 遍历&计数
            AtomicInteger successNumber = new AtomicInteger();
            List<SceneManageEntity> sceneList = getSceneListByScriptId(scriptId);
            sceneList.forEach(t -> {
                boolean itemSynchronizeResult = synchronize(t.getId(), threadGroupMd5, samplerMd5, controllerMd5, businessActivityRef);
                if (itemSynchronizeResult) {successNumber.getAndIncrement();}
            });
            String onceTransactionIdentifier = transactionIdentifier.get();
            transactionIdentifier.remove();
            log.info("同步场景信息:{},同步结果:{}/{}.", onceTransactionIdentifier, successNumber, sceneList.size());
            return onceTransactionIdentifier;
        }

    }

    private boolean synchronize(long sceneId, Set<String> threadGroupMd5, Set<String> samplerMd5, Set<String> controllerMd5, Map<String, Long> businessActivityRef) {
        // 手动事务控制
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
        try {
            // 同步线程组配置
            if (synchronizeThreadGroupConfig(sceneId, threadGroupMd5)) {
                // 同步场景节点
                if (synchronizeSceneNode(sceneId, samplerMd5, businessActivityRef, controllerMd5)) {
                    platformTransactionManager.commit(transactionStatus);
                    return true;
                }
            }
            // 上述模块全部同步成功后才提交数据，否则回滚数据
            platformTransactionManager.rollback(transactionStatus);
            // TODO 标识场景为不可压测
            return false;
        } catch (Exception e) {
            // 发生异常则回滚数据
            platformTransactionManager.rollback(transactionStatus);
            return false;
        }
    }

    /**
     * 同步压测场景节点
     *
     * @param sceneId             场景主键
     * @param samplerNode         采样器节点
     * @param businessActivityRef 业务活动关联关系
     * @param controllerNode      控制器节点
     * @return 匹配结果
     */
    private boolean synchronizeSceneNode(long sceneId, Set<String> samplerNode, Map<String, Long> businessActivityRef, Set<String> controllerNode) {
        // 组装全部节点
        Set<String> allNodeMd5 = new HashSet<>(samplerNode);
        allNodeMd5.addAll(controllerNode);
        // 同步(场景节点、压测目标、SLA)
        return synchronizeGoal(sceneId, new HashSet<>(allNodeMd5), businessActivityRef)
            && synchronizeMonitoringGoal(sceneId, new HashSet<>(allNodeMd5));
    }

    /**
     * 匹配压测目标及场景节点
     *
     * @param sceneId             场景主键
     * @param allNodeMd5          全部节点的MD5值
     * @param businessActivityRef 业务活动关联关系
     * @return 匹配结果
     */
    private boolean synchronizeGoal(long sceneId, Set<String> allNodeMd5, Map<String, Long> businessActivityRef) {
        // 开始匹配
        //  1. 获取历史数据
        Map<String, SceneRequest.Goal> sceneGoal = sceneService.getGoal(sceneId);
        Map<String, SceneRequest.Content> sceneContent = sceneService.getContent(sceneId);
        // 2.组装历史的节点信息
        Set<String> currentNodeMd5 = new HashSet<>(sceneGoal.keySet());
        currentNodeMd5.addAll(sceneContent.keySet());
        // 拷贝临时变量
        Set<String> allNodeMd5Copy = new HashSet<>(allNodeMd5);
        // 3. 判断是否是可以同步
        //      新配置少于或等于旧配置数量，就可以同步
        currentNodeMd5.forEach(allNodeMd5Copy::remove);
        if (allNodeMd5Copy.size() > 0) {
            log.info("事务:{}.场景{}.同步失败.压测目标匹配失败.", transactionIdentifier.get(), sceneId);
            return false;
        }
        // 开始同步
        //  1. 删除本次未匹配上的
        allNodeMd5.forEach(t -> {
            if (!sceneGoal.containsKey(t)) {
                sceneGoal.remove(t);
                sceneContent.remove(t);
            }
        });
        //  2. 更新节点匹配的应用信息
        sceneContent.forEach((k, v) -> v.setBusinessActivityId(businessActivityRef.get(k)));
        //  3. 更新配置信息
        List<SceneRequest.Content> contentList = new ArrayList<>(sceneContent.size());
        sceneContent.forEach((k, v) -> {
            v.setPathMd5(k);
            contentList.add(v);
        });
        // 重新填充数据
        int activityClearRows = sceneBusinessActivityRefMapper.delete(Wrappers.lambdaUpdate(SceneBusinessActivityRefEntity.class)
            .eq(SceneBusinessActivityRefEntity::getSceneId, sceneId));
        log.info("事务:{}.场景{}.更新管理业务活动信息。\n清理业务活动数据:{}。", transactionIdentifier.get(), sceneId, activityClearRows);
        sceneService.buildBusinessActivity(sceneId, contentList, sceneGoal);
        return true;
    }

    /**
     * 匹配SLA
     *
     * @param sceneId    场景主键
     * @param allNodeMd5 全部节点的MD5值
     * @return 匹配结果
     */
    private boolean synchronizeMonitoringGoal(long sceneId, HashSet<String> allNodeMd5) {
        Map<Long, List<String>> readyUpdateSla = new HashMap<>(allNodeMd5.size());
        List<SceneRequest.MonitoringGoal> monitoringGoal = sceneService.getMonitoringGoal(sceneId);
        // 遍历SLA
        for (SceneRequest.MonitoringGoal goal : monitoringGoal) {
            HashSet<String> itemTarget = new HashSet<>(goal.getTarget());
            List<String> newItemTarget = new ArrayList<>(itemTarget.size());
            // 遍历目标
            for (String target : itemTarget) {
                // 筛选匹配
                if ("all".equals(target) || allNodeMd5.contains(target)) {newItemTarget.add(target);}
            }
            // 填充入待更新项
            if (itemTarget.size() != newItemTarget.size()) {readyUpdateSla.put(goal.getId(), newItemTarget);}
        }
        readyUpdateSla.forEach((k, v) -> {
            // 如果没有匹配的目标，则删除SLA
            if (v.size() == 0) {sceneSlaRefMapper.deleteById(k);}
            // 否则更新目标值
            sceneSlaRefMapper.updateById(new SceneSlaRefEntity() {{
                setSceneId(k);
                setBusinessActivityIds(String.join(",", v));
            }});
        });
        return true;
    }

    /**
     * 同步线程组施压配置
     *
     * @param sceneId        场景主键
     * @param threadGroupMd5 新的线程组
     * @return 同步结果
     */
    private boolean synchronizeThreadGroupConfig(long sceneId, Set<String> threadGroupMd5) {
        SceneManageEntity scene = sceneManageMapper.selectById(sceneId);
        String ptConfigString = scene.getPtConfig();
        if (StrUtil.isBlank(ptConfigString)) {return false;}
        try {
            // 反序列化施压配置信息
            PtConfigExt ptConfig = JSONObject.parseObject(ptConfigString, new TypeReference<PtConfigExt>() {});
            // 获取线程组配置
            Map<String, ThreadGroupConfigExt> threadGroupConfigMap = ptConfig.getThreadGroupConfigMap();
            // 备份新参数
            Set<String> threadGroupMd5Copy = new HashSet<>(threadGroupMd5);
            // 判断是否是可以同步
            //      新配置少于或等于旧配置数量，就可以同步
            threadGroupConfigMap.keySet().forEach(threadGroupMd5Copy::remove);
            if (threadGroupMd5Copy.size() > 0) {
                log.info("事务:{}.场景{}同步失败.线程组施压配置匹配失败", transactionIdentifier.get(), sceneId);
                return false;
            }
            // 开始同步
            //  1. 删除本次未匹配上的
            threadGroupMd5.forEach(t -> {
                if (!threadGroupConfigMap.containsKey(t)) {threadGroupConfigMap.remove(t);}
            });
            //  2. 更新配置信息
            sceneManageMapper.updateById(new SceneManageEntity() {{
                setId(sceneId);
                setPtConfig(JSONObject.toJSONString(ptConfig));
            }});
            return true;
        } catch (Exception e) {
            log.error("同步场景.事务{}.场景主键:{}.同步线程组施压配置失败.", transactionIdentifier.get(), sceneId, e);
            return false;
        }
    }

    /**
     * 对脚本解析结果进行分组
     *
     * @param analysisResult 脚本解析结果
     * @param container      分组容器  &lt;节点类型,对应类型的节点集合&gt;
     */
    private void groupByAnalysisResult(List<ScriptNode> analysisResult, Map<NodeTypeEnum, List<ScriptNode>> container) {
        analysisResult.forEach(t -> {
            // 根据类型分类
            NodeTypeEnum groupKey = t.getType();
            // 类别初始化
            if (!container.containsKey(groupKey)) {container.put(groupKey, new LinkedList<>());}
            // 数据入桶
            container.get(groupKey).add(t);
            if (t.getChildren() != null) {groupByAnalysisResult(t.getChildren(), container);}
        });
    }

    /**
     * 根据脚本主键获取压测场景集合
     *
     * @param scriptId 脚本主键
     * @return 压测场景集合
     */
    private List<SceneManageEntity> getSceneListByScriptId(long scriptId) {
        // 从数据库获取全部的场景
        // TODO 租户隔离
        List<SceneManageEntity> sceneList = sceneManageMapper.selectList(Wrappers.lambdaQuery(SceneManageEntity.class)
            .eq(SceneManageEntity::getIsDeleted, false)
            .isNotNull(SceneManageEntity::getScriptAnalysisResult)
            .ne(SceneManageEntity::getScriptAnalysisResult, ""));
        // 根据脚本主键过滤
        return sceneList.stream().filter(t -> {
            String featureString = t.getFeatures();
            if (StrUtil.isBlank(featureString)) {return false;}
            try {
                Map<String, Object> features = JSONObject.parseObject(featureString, new TypeReference<Map<String, Object>>() {});
                if (features.containsKey("scriptId")) {
                    return String.valueOf(scriptId).equals(features.get("scriptId"));
                } else {return false;}
            } catch (Exception e) {
                return false;
            }
        }).collect(Collectors.toList());
    }

}
