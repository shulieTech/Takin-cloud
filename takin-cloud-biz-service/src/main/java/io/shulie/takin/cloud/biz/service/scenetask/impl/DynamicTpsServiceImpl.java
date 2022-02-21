package io.shulie.takin.cloud.biz.service.scenetask.impl;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.util.StrUtil;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;

import io.shulie.takin.cloud.common.utils.JmxUtil;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.biz.service.scene.SceneService;
import io.shulie.takin.cloud.biz.service.report.ReportService;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.biz.output.report.ReportDetailOutput;
import io.shulie.takin.cloud.biz.service.scenetask.DynamicTpsService;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;

/**
 * 动态TPS服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class DynamicTpsServiceImpl implements DynamicTpsService {
    @Resource
    SceneService sceneService;
    @Resource
    ReportService reportService;
    @Resource
    StringRedisTemplate stringRedisTemplate;

    public static final String MAP_KEY = "REDIS_TPS_MAP";
    /**
     * 这个字段很操蛋,因为引擎那边的Redis工具类封装的很麻烦,暂时用这个方式
     */
    public static final String ALL_ITEM_KEY = "REDIS_TPS_ALL_KEY";
    public static final String PRESSURE_ENGINE_INSTANCE_REDIS_KEY = "PRESSURE:ENGINE:INSTANCE";

    /**
     * 获取动态TPS目标值
     *
     * @param sceneId  场景主键
     * @param reportId 报告主键
     * @param tenantId 租户主键
     * @param md5      脚本节点md5
     * @return 值-可能为空
     */
    @Override
    public Double get(long sceneId, long reportId, long tenantId, String md5) {
        // 转换节点MD5为线程组MD5
        md5 = getThreadGroupMd5ByXpathMd5(sceneId, md5);
        // 获取全部项
        List<String> allItem = getAllItem(sceneId, reportId, tenantId);
        // 如果AllItem中没有,返回空
        if (!allItem.contains(md5)) {return null;}
        Object valueString = stringRedisTemplate.opsForHash().get(getMapKey(sceneId, reportId, tenantId), md5);
        // 如果Redis中没有值,返回空
        if (valueString == null) {return null;}
        // 进行数值转换并返回
        return Double.valueOf(valueString.toString());
    }

    /**
     * 通过报告获取静态TPS目标
     *
     * @param reportId 报告主键
     * @param xpathMd5 脚本节点md5
     * @return TPS目标
     */
    @Override
    public double getStatic(long reportId, String xpathMd5) {
        // 获取报告
        ReportDetailOutput report = reportService.getReportByReportId(reportId);
        // 转换节点MD5为线程组MD5
        String md5 = getThreadGroupMd5ByXpathMd5(report.getSceneId(), xpathMd5);
        // 获取场景
        SceneManageEntity scene = sceneService.getScene(report.getSceneId());
        // 获取脚本解析结果
        String scriptAnalysisString = scene.getScriptAnalysisResult();
        List<ScriptNode> scriptAnalysis = JSON.parseArray(scriptAnalysisString, ScriptNode.class);
        //    脚本解析结果-一维展开
        List<ScriptNode> oneDepthScriptAnalysis = JmxUtil.toOneDepthList(scriptAnalysis);
        //    找到线程组节点
        if (oneDepthScriptAnalysis == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.COMMON_VERIFY_ERROR, "脚本解析错误");
        }
        ScriptNode scriptNode = oneDepthScriptAnalysis.stream().filter(t -> md5.equals(t.getXpathMd5())).findAny().orElse(null);
        if (scriptNode == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.COMMON_VERIFY_ERROR, "未找到线程组节点");
        }
        //    获取线程组节点下所有
        if (scriptNode.getChildren() == null || scriptNode.getChildren().size() == 0) {
            throw new TakinCloudException(TakinCloudExceptionEnum.COMMON_VERIFY_ERROR, "线程组下没有其他节点");
        }
        //TODO 应该遍历出所有节点,然后找匹配的业务活动,然后算出总和的TPS
        throw new TakinCloudException(TakinCloudExceptionEnum.COMMON_VERIFY_ERROR, "暂未实现");
    }

    /**
     * 设置动态TPS目标值
     *
     * @param sceneId  场景主键
     * @param reportId 报告主键
     * @param tenantId 租户主键
     * @param md5      脚本节点md5
     * @param value    目标值
     */
    @Override
    public synchronized void set(long sceneId, long reportId, long tenantId, String md5, double value) {
        // 转换节点MD5为线程组MD5
        md5 = getThreadGroupMd5ByXpathMd5(sceneId, md5);
        // 获取全部项
        List<String> allItem = getAllItem(sceneId, reportId, tenantId);
        // 如果是新设定的,要在AllItem中添加项
        if (!allItem.contains(md5)) {
            allItem.add(md5);
            stringRedisTemplate.opsForValue().set(getAllItemKey(sceneId, reportId, tenantId), JSON.toJSONString(allItem));
        }
        // 设定map值
        stringRedisTemplate.opsForHash().put(getMapKey(sceneId, reportId, tenantId), md5, String.valueOf(value));
    }

    /**
     * 根据节点MD5获取对应的线程组的MD5
     *
     * @param sceneId  场景主键
     * @param xpathMd5 节点MD5
     * @return 线程组MD5
     */
    private String getThreadGroupMd5ByXpathMd5(long sceneId, String xpathMd5) {
        // 获取场景
        SceneManageEntity scene = sceneService.getScene(sceneId);
        // 获取脚本解析结果
        String scriptAnalysisString = scene.getScriptAnalysisResult();
        List<ScriptNode> scriptAnalysis = JSON.parseArray(scriptAnalysisString, ScriptNode.class);
        if (scriptAnalysis != null && scriptAnalysis.size() == 1) {
            HashMap<String, String> container = new HashMap<>(scriptAnalysis.size());
            scriptAnalysis.get(0).getChildren().forEach(t -> fullThreadGroupWhitNodeRelation(container, t.getXpathMd5(), t));
            String md5 = container.get(xpathMd5);
            if (StrUtil.isBlank(md5)) {
                throw new TakinCloudException(TakinCloudExceptionEnum.COMMON_VERIFY_ERROR, "未找到节点所对应的线程组节点");
            }
            return md5;
        }
        throw new TakinCloudException(TakinCloudExceptionEnum.COMMON_VERIFY_ERROR, "脚本解析结果不正确");
    }

    /**
     * 填充线程组和节点的关系
     *
     * @param container      容器
     *                       <ul>
     *                       <li>key:节点MD5</li>
     *                       <li>value:线程组MD5</li>
     *                       </ul>
     * @param threadGroupMd5 线程组MD5
     * @param node           节点
     */
    private void fullThreadGroupWhitNodeRelation(Map<String, String> container, String threadGroupMd5, ScriptNode node) {
        // 填充本节点
        container.put(node.getXpathMd5(), threadGroupMd5);
        // 填充子节点
        if (node.getChildren() != null) {
            node.getChildren().forEach(t -> fullThreadGroupWhitNodeRelation(container, threadGroupMd5, t));
        }
    }

    /**
     * 获取数据溯源部分的字符串
     *
     * @param sceneId  场景主键
     * @param reportId 报告主键
     * @param tenantId 租户主键
     * @return 溯源字符串
     */
    private String getDataTracePart(long sceneId, long reportId, long tenantId) {
        return String.format("%s:%s:%s", sceneId, reportId, tenantId);
    }

    /**
     * 获取redis中Map的Key
     *
     * @param sceneId  场景主键
     * @param reportId 报告主键
     * @param tenantId 租户主键
     * @return map对应的key
     */
    private String getMapKey(long sceneId, long reportId, long tenantId) {
        String dataTracePart = getDataTracePart(sceneId, reportId, tenantId);
        return StrUtil.format("{}:{}:{}", PRESSURE_ENGINE_INSTANCE_REDIS_KEY, dataTracePart, MAP_KEY);
    }

    /**
     * 获取redis中AllItem的Key
     *
     * @param sceneId  场景主键
     * @param reportId 报告主键
     * @param tenantId 租户主键
     * @return AllItem对应的key
     */
    private String getAllItemKey(long sceneId, long reportId, long tenantId) {
        String dataTracePart = getDataTracePart(sceneId, reportId, tenantId);
        return StrUtil.format("{}:{}:{}", PRESSURE_ENGINE_INSTANCE_REDIS_KEY, dataTracePart, ALL_ITEM_KEY);
    }

    /**
     * 获取AllItem
     *
     * @param sceneId  场景主键
     * @param reportId 报告主键
     * @param tenantId 租户主键
     * @return AllItem
     */
    private List<String> getAllItem(long sceneId, long reportId, long tenantId) {
        List<String> itemList;
        String allItemString = stringRedisTemplate.opsForValue().get(getAllItemKey(sceneId, reportId, tenantId));
        if (StrUtil.isBlank(allItemString)) {
            itemList = new ArrayList<>(0);
        } else {
            itemList = JSON.parseArray(allItemString, String.class);
        }
        return itemList;
    }

}
