package io.shulie.takin.cloud.biz.service.scene.impl;

import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.LocalDateTime;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.biz.service.scene.SceneService;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneSlaRefEntity;
import io.shulie.takin.cloud.data.mapper.mysql.SceneSlaRefMapper;
import io.shulie.takin.cloud.data.mapper.mysql.SceneManageMapper;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.data.model.mysql.SceneScriptRefEntity;
import io.shulie.takin.cloud.data.mapper.mysql.SceneScriptRefMapper;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest;
import io.shulie.takin.cloud.data.model.mysql.SceneBusinessActivityRefEntity;
import io.shulie.takin.cloud.data.mapper.mysql.SceneBusinessActivityRefMapper;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.Goal;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.File;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.Config;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.Content;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.MonitoringGoal;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.DataValidation;

/**
 * 场景 - 服务实现
 *
 * @author 张天赐
 */
@Slf4j
@Service
public class SceneServiceImpl implements SceneService {
    @Resource
    SceneManageMapper sceneManageMapper;
    @Resource
    SceneSlaRefMapper sceneSlaRefMapper;
    @Resource
    SceneManageService sceneManageService;
    @Resource
    SceneScriptRefMapper sceneScriptRefMapper;
    @Resource
    SceneBusinessActivityRefMapper sceneBusinessActivityRefMapper;

    /**
     * 创建压测场景
     *
     * @param in 入参
     * @return 场景主键
     */
    @Override
    public Long create(WriteSceneRequest in) {
        // 1.   创建场景
        long sceneId = createStepScene(in.getName(), in.getType(), in.getScriptType(), in.getScriptId(),
            in.getBusinessFlowId(), in.getConfig(), in.getAnalysisResult(), in.getDataValidation());
        // 2. 更新场景&业务活动关联关系
        createStepBusinessActivity(sceneId, in.getContent(), in.getGoal());
        // 3.   处理脚本
        createStepScript(sceneId, in.getScriptId(), in.getScriptType(), in.getFile());
        // 4.  保存SLA信息
        createStepSla(sceneId, in.getMonitoringGoal());
        //      返回信息
        return sceneId;
    }

    /**
     * 创建压测场景 - 步骤1 : 基础信息
     *
     * @param name           场景名称
     * @param type           场景类型
     *                       <ul>
     *                           <li>0:普通场景</li>
     *                           <li>1:流量调试场景</li>
     *                       </ul>
     * @param scriptType     脚本类型
     *                       <ul>
     *                           <li>0:Jmeter</li>
     *                           <li>1:Gatling</li>
     *                       </ul>
     * @param scriptId       脚本实例主键
     * @param businessFlowId 业务流程主键
     * @param config         施压配置
     * @param analysisResult 脚本解析结果
     * @param dataValidation 数据验证配置
     * @return 压测场景主键
     */
    private Long createStepScene(String name, int type, int scriptType, long scriptId, long businessFlowId,
        Map<String, Config> config, List<?> analysisResult, DataValidation dataValidation) {
        Map<String, Object> feature = new HashMap<String, Object>(4) {{
            put("scriptId", scriptId);
            put("analysisResult", analysisResult);
            put("businessFlowId", businessFlowId);
            put("dataValidation", dataValidation);
        }};
        SceneManageEntity sceneEntity = new SceneManageEntity() {{
            setSceneName(name);
            setFeatures(JSONObject.toJSONString(feature));
            setPtConfig(JSONObject.toJSONString(config));
            setStatus(0);
            setType(type);
            setUserId(-1L);
            setDeptId(null);
            setCustomerId(null);
            setLastPtTime(null);
            setScriptType(scriptType);
            setIsDeleted(0);
            Date now = new Date();
            setCreateTime(now);
            setUpdateTime(now);
            setCreateName(null);
            setUpdateName(null);
        }};
        sceneManageMapper.insert(sceneEntity);
        long sceneId = sceneEntity.getId();
        log.info("创建了业务活动「{}」。自增主键：{}.", name, sceneId);
        return sceneId;
    }

    /**
     * 创建压测场景 - 步骤2 : 关联业务活动
     *
     * @param sceneId 场景主键
     * @param content 压测内容
     * @param goalMap 压测目标
     */
    private void createStepBusinessActivity(long sceneId, List<Content> content, Map<String, Goal> goalMap) {
        for (Content t : content) {
            Goal goal = goalMap.get(t.getPathMd5());
            SceneBusinessActivityRefEntity activityRef = new SceneBusinessActivityRefEntity() {{
                setSceneId(sceneId);
                setBindRef(t.getPathMd5());
                setCreateName(t.getName());
                setBusinessActivityId(t.getBusinessActivityId());
                setApplicationIds(String.join(",", t.getApplicationId()));
                setGoalValue(JSONObject.toJSONString((goal == null ? new Goal() : goal), SerializerFeature.PrettyFormat));
                // 其它字段默认值
                LocalDateTime now = LocalDateTime.now();
                setIsDeleted(0);
                setCreateTime(now);
                setUpdateTime(now);
                setCreateName(null);
                setUpdateName(null);
            }};
            sceneBusinessActivityRefMapper.insert(activityRef);
            log.info("业务活动{}关联了业务活动{}-{}。自增主键：{}.", sceneId, t.getBusinessActivityId(), t.getPathMd5(), activityRef.getId());
        }
    }

    /**
     * 创建压测场景 - 步骤3 : 关联压测文件
     *
     * @param sceneId    场景主键
     * @param scriptId   脚本主键
     * @param scriptType 脚本类型
     * @param file       压测文件
     */
    private void createStepScript(long sceneId, Long scriptId, Integer scriptType, List<File> file) {
        List<SceneScriptRefEntity> sceneScriptRefEntityList = new ArrayList<>(file.size());
        for (File t : file) {
            String fileName = FileUtil.getName(t.getPath());
            String destPath = sceneManageService.getDestPath(sceneId);
            switch (t.getType()) {
                case 0:
                case 1:
                    break;
                case 2:
                    destPath = destPath + SceneManageConstant.FILE_SPLIT + "attachments";
                    break;
                default:
                    log.info("遇到{}类型的文件:[{}]", t.getType(), t.getPath());
                    break;
            }
            if (StrUtil.isNotBlank(destPath)) {
                String filePath = destPath + SceneManageConstant.FILE_SPLIT + fileName;
                FileUtil.copy(t.getPath(), filePath, true);
                sceneScriptRefEntityList.add(new SceneScriptRefEntity() {{
                    setSceneId(sceneId);
                    setFileName(fileName);
                    setUploadPath(filePath);
                    setFileType(t.getType());
                    setScriptType(scriptType);
                    setFileExtend(JSONObject.toJSONString(t.getExtend()));
                    // 其它字段默认值
                    setFileSize(null);
                    LocalDateTime now = LocalDateTime.now();
                    setIsDeleted(0);
                    setCreateTime(now);
                    setUpdateTime(now);
                    setCreateName(null);
                    setUpdateName(null);
                }});
            }
        }
        //  2.1 更新场景&脚本关联关系
        for (SceneScriptRefEntity entity : sceneScriptRefEntityList) {
            sceneScriptRefMapper.insert(entity);
            log.info("业务活动{}关联了文件{}-{}。自增主键：{}.", sceneId, scriptId, entity.getFileName(), entity.getId());
        }
    }

    /**
     * 创建压测场景 - 步骤4 : 关联SLA
     *
     * @param sceneId        场景主键
     * @param monitoringGoal 监控目标
     */
    private void createStepSla(long sceneId, List<MonitoringGoal> monitoringGoal) {
        for (MonitoringGoal mGoal : monitoringGoal) {
            SceneSlaRefEntity entity = new SceneSlaRefEntity() {{
                Map<String, Object> condition = new HashMap<String, Object>(4) {{
                    put(SceneManageConstant.COMPARE_VALUE, mGoal.getFormulaNumber());
                    put(SceneManageConstant.COMPARE_TYPE, mGoal.getFormulaSymbol());
                    put(SceneManageConstant.EVENT, mGoal.getType() == 0 ?
                        SceneManageConstant.EVENT_DESTORY : SceneManageConstant.EVENT_WARN);
                    put(SceneManageConstant.ACHIEVE_TIMES, mGoal.getNumberOfIgnore());
                }};
                setSceneId(sceneId);
                setTargetType(mGoal.getFormulaTarget());
                setCondition(JSONObject.toJSONString(condition));
                setBusinessActivityIds(String.join(",", mGoal.getTarget()));
                // 其它字段默认值
                setStatus(0);
                LocalDateTime now = LocalDateTime.now();
                setIsDeleted(0);
                setCreateTime(now);
                setUpdateTime(now);
                setCreateName(null);
                setUpdateName(null);
            }};
            sceneSlaRefMapper.insert(entity);
            log.info("业务活动{}关联了SLA{}。自增主键：{}.", sceneId, mGoal.getName(), entity.getId());
        }
    }
}
