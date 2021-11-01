package io.shulie.takin.cloud.biz.service.scene.impl;

import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.serializer.SerializerFeature;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import io.shulie.takin.cloud.biz.service.scene.SceneService;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneSlaRefEntity;
import io.shulie.takin.cloud.data.mapper.mysql.SceneSlaRefMapper;
import io.shulie.takin.cloud.data.mapper.mysql.SceneManageMapper;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.data.model.mysql.SceneScriptRefEntity;
import io.shulie.takin.cloud.data.mapper.mysql.SceneScriptRefMapper;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest;
import io.shulie.takin.cloud.open.response.scene.manage.SceneDetailResponse;
import io.shulie.takin.cloud.data.model.mysql.SceneBusinessActivityRefEntity;
import io.shulie.takin.cloud.data.mapper.mysql.SceneBusinessActivityRefMapper;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.Goal;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.File;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.Config;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.Content;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.BasicInfo;
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
        long sceneId = createStepScene(in.getBasicInfo(), in.getConfig(), in.getAnalysisResult(), in.getDataValidation());
        // 2. 更新场景&业务活动关联关系
        createStepBusinessActivity(sceneId, in.getContent(), in.getGoal());
        // 3.   处理脚本
        createStepScript(sceneId, in.getBasicInfo().getScriptId(), in.getBasicInfo().getScriptType(), in.getFile());
        // 4.  保存SLA信息
        createStepSla(sceneId, in.getMonitoringGoal());
        //      返回信息
        return sceneId;
    }

    /**
     * 更新压测场景
     *
     * @param in 入参
     * @return 场景主键
     */
    @Override
    public Boolean update(WriteSceneRequest in) {
        // 0.   清除历史项
        {
            Long sceneId = in.getBasicInfo().getSceneId();
            int slaClearRows = sceneSlaRefMapper.delete(Wrappers.lambdaUpdate(SceneSlaRefEntity.class).eq(SceneSlaRefEntity::getSceneId, sceneId));
            int fileClearRows = sceneScriptRefMapper.delete(Wrappers.lambdaUpdate(SceneScriptRefEntity.class).eq(SceneScriptRefEntity::getSceneId, sceneId));
            int activityClearRows = sceneBusinessActivityRefMapper.delete(Wrappers.lambdaUpdate(SceneBusinessActivityRefEntity.class).eq(SceneBusinessActivityRefEntity::getSceneId, sceneId));
            log.info("更新压测场景。\n清理sla数据:{}。\n清理文件数据:{}。\n清理业务活动数据:{}。", slaClearRows, fileClearRows, activityClearRows);
        }
        // 1.   创建场景
        int updateRows = updateStepScene(in.getBasicInfo(), in.getConfig(), in.getAnalysisResult(), in.getDataValidation());
        if (updateRows == 0) {return false;}
        if (updateRows == 1) {
            long sceneId = in.getBasicInfo().getSceneId();
            // 2. 更新场景&业务活动关联关系
            createStepBusinessActivity(sceneId, in.getContent(), in.getGoal());
            // 3.   处理脚本
            createStepScript(sceneId, in.getBasicInfo().getScriptId(), in.getBasicInfo().getScriptType(), in.getFile());
            // 4.  保存SLA信息
            createStepSla(sceneId, in.getMonitoringGoal());
            //      返回信息
            return true;
        } else {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_UPDATE_ERROR, "意外的逻辑");
        }
    }

    /**
     * 获取场景详情
     *
     * @param sceneId 场景主键
     * @return 场景详情
     */
    @Override
    public SceneDetailResponse detail(long sceneId) {
        return new SceneDetailResponse() {{
            setGoal(getGoal());
            setConfig(getConfig());
            setContent(getContent());
            setBasicInfo(getBasicInfo());
            setAnalysisResult(getAnalysisResult());
            setDataValidation(getDataValidation());
            setMonitoringGoal(getMonitoringGoal());
        }};
    }

    /**
     * 获取场景的基础信息
     *
     * @param sceneId 场景主键
     * @return 基础信息
     */
    @Override
    public BasicInfo getBasicInfo(long sceneId) {
        try {
            SceneManageEntity scene = getScene(sceneId);
            // 解析拓展字段
            String featureString = scene.getFeatures();
            Map<String, ?> feature = JSONObject.parseObject(featureString, new TypeReference<Map<String, ?>>() {});
            // 获取值
            String scriptIdString, businessFlowIdString;
            Object scriptIdResult = feature.get("scriptId");
            scriptIdString = scriptIdResult == null ? "-1" : scriptIdResult.toString();
            Object businessFlowIdResult = feature.get("businessFlowId");
            businessFlowIdString = businessFlowIdResult == null ? "-1" : businessFlowIdResult.toString();
            // 组装返回数据
            return new BasicInfo() {{
                setSceneId(scene.getId());
                setName(scene.getSceneName());
                setType(scene.getType());
                setScriptType(scene.getScriptType());
                setScriptId(Long.parseLong(scriptIdString));
                setBusinessFlowId(Long.parseLong(businessFlowIdString));
            }};
        } catch (JSONException e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, sceneId + "的拓展字段错误");
        }
    }

    /**
     * 获取脚本解析结果
     *
     * @param sceneId 场景主键
     * @return 解析结果
     */
    @Override
    public List<?> getAnalysisResult(long sceneId) {
        try {
            SceneManageEntity scene = getScene(sceneId);
            String featureString = scene.getFeatures();
            // 解析拓展字段
            Map<String, ?> feature = JSONObject.parseObject(featureString, new TypeReference<Map<String, ?>>() {});
            Object analysisResult = feature.get("analysisResult");
            if (analysisResult == null) {
                throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, sceneId + "的拓展字段缺失[脚本解析结果]");
            }
            return (List<?>)analysisResult;
        } catch (JSONException e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, sceneId + "的拓展字段错误");
        }
    }

    /**
     * 获取数据验证配置
     *
     * @param sceneId 场景主键
     * @return 数据验证配置
     */
    @Override
    public DataValidation getDataValidation(long sceneId) {
        try {
            SceneManageEntity scene = getScene(sceneId);
            String featureString = scene.getFeatures();
            // 解析拓展字段
            Map<String, ?> feature = JSONObject.parseObject(featureString, new TypeReference<Map<String, ?>>() {});
            Object dataValidationResult = feature.get("dataValidation");
            if (dataValidationResult == null) {
                throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, sceneId + "的拓展字段缺失[数据验证配置]");
            }
            return (DataValidation)dataValidationResult;
        } catch (JSONException e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, sceneId + "的拓展字段错误");
        }
    }

    /**
     * 获取压测内容
     *
     * @param sceneId 场景主键
     * @return 压测目标<节点MD5, 目标对象>
     */
    @Override
    public Map<String, Content> getContent(long sceneId) {
        try {
            SceneManageEntity scene = getScene(sceneId);
            // 获取关联的信息
            List<SceneBusinessActivityRefEntity> activityRefList = sceneBusinessActivityRefMapper.selectList(Wrappers.lambdaQuery(SceneBusinessActivityRefEntity.class)
                .eq(SceneBusinessActivityRefEntity::getSceneId, scene.getId()));
            // 构建mao结构
            Map<String, SceneBusinessActivityRefEntity> entityResult = activityRefList.stream()
                .collect(Collectors.toMap(SceneBusinessActivityRefEntity::getBindRef, t -> t));
            Map<String, Content> result = new HashMap<>(entityResult.size());
            // 填充结果
            entityResult.forEach((key, value) -> result.put(key, new Content() {{
                setPathMd5(value.getBindRef());
                setName(value.getBusinessActivityName());
                setBusinessActivityId(value.getBusinessActivityId());
                setApplicationId(Arrays.asList(value.getApplicationIds().split(",")));
            }}));
            return result;
        } catch (
            JSONException e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, sceneId + "施压目标解析错误");
        }
    }

    /**
     * 获取压测目标
     *
     * @param sceneId 场景主键
     * @return 压测目标<节点MD5, 目标对象>
     */
    @Override
    public Map<String, Goal> getGoal(long sceneId) {
        try {
            SceneManageEntity scene = getScene(sceneId);
            // 获取关联的信息
            List<SceneBusinessActivityRefEntity> activityRefList = sceneBusinessActivityRefMapper.selectList(Wrappers.lambdaQuery(SceneBusinessActivityRefEntity.class)
                .eq(SceneBusinessActivityRefEntity::getSceneId, scene.getId()));
            // 构建mao结构
            Map<String, String> stringResult = activityRefList.stream()
                .collect(Collectors.toMap(SceneBusinessActivityRefEntity::getBindRef, SceneBusinessActivityRefEntity::getGoalValue));
            Map<String, Goal> result = new HashMap<>(stringResult.size());
            // 填充结果
            stringResult.forEach((key, value) -> result.put(key, JSONObject.parseObject(value, Goal.class)));
            return result;
        } catch (
            JSONException e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, sceneId + "施压目标解析错误");
        }
    }

    /**
     * 获取压测线程组配置
     *
     * @param sceneId 场景主键
     * @return 线程组配置<节点MD5, 配置对象>
     */
    @Override
    public Config getConfig(long sceneId) {
        try {
            SceneManageEntity scene = getScene(sceneId);
            return JSONObject.parseObject(scene.getPtConfig(), new TypeReference<Config>() {});
        } catch (JSONException e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, sceneId + "压测线程组解析错误");
        }
    }

    /**
     * 获取压测SLA
     *
     * @param sceneId 场景主键
     * @return SLA列表
     */
    @Override
    public List<MonitoringGoal> getMonitoringGoal(long sceneId) {
        try {
            SceneManageEntity scene = getScene(sceneId);
            // 获取关联的信息
            List<SceneSlaRefEntity> slaResultList = sceneSlaRefMapper.selectList(Wrappers.lambdaQuery(SceneSlaRefEntity.class)
                .eq(SceneSlaRefEntity::getSceneId, scene.getId()));
            // 构建mao结构
            return slaResultList.stream().map(t -> {
                Map<String, String> condition = JSONObject.parseObject(t.getCondition(), new TypeReference<Map<String, String>>() {});
                String eventString = condition.getOrDefault(SceneManageConstant.EVENT, "");
                String compareTypeString = condition.getOrDefault(SceneManageConstant.COMPARE_TYPE, "0");
                String achieveTimesString = condition.getOrDefault(SceneManageConstant.ACHIEVE_TIMES, "0");
                String compareValueString = condition.getOrDefault(SceneManageConstant.COMPARE_VALUE, "0");
                return new MonitoringGoal() {{
                    setType(eventString.equals(SceneManageConstant.EVENT_DESTORY) ? 0 : 1);
                    setFormulaNumber(Double.parseDouble(compareValueString));
                    setFormulaSymbol(Integer.parseInt(compareTypeString));
                    setName(t.getSlaName());
                    setFormulaSymbol(t.getTargetType());
                    setTarget(Arrays.asList(t.getBusinessActivityIds().split(",")));
                    setNumberOfIgnore(Integer.parseInt(achieveTimesString));
                }};
            }).collect(Collectors.toList());
        } catch (
            JSONException e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, sceneId + "SLA条件错误");
        }
    }

    /**
     * 获取场景的表信息
     *
     * @param sceneId 场景主键
     * @return 场景[表信息]
     * @throws TakinCloudException 未找到场景
     */
    private SceneManageEntity getScene(long sceneId) throws TakinCloudException {
        SceneManageEntity scene = sceneManageMapper.selectById(sceneId);
        if (scene == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, "未找到场景:" + sceneId);
        }
        return scene;
    }

    /**
     * 创建压测场景 - 步骤1 : 基础信息
     *
     * @param basicInfo      基础信息
     *                       <p>场景名称</p>
     *                       <p>场景类型
     *                       <ul>
     *                           <li>0:普通场景</li>
     *                           <li>1:流量调试场景</li>
     *                       </ul>
     *                       </p>
     *                       <p>
     *                       脚本类型
     *                       <ul>
     *                           <li>0:Jmeter</li>
     *                           <li>1:Gatling</li>
     *                       </ul>
     *                       </p>
     *                       <p>脚本实例主键</p>
     *                       <p>业务流程主键</p>
     * @param config         施压配置
     * @param analysisResult 脚本解析结果
     * @param dataValidation 数据验证配置
     * @return 压测场景主键
     */
    private Long createStepScene(BasicInfo basicInfo,
        Config config, List<?> analysisResult, DataValidation dataValidation) {
        Map<String, Object> feature = assembleFeature(basicInfo.getScriptId(), basicInfo.getBusinessFlowId(), analysisResult, dataValidation);
        // 组装数据实体类
        SceneManageEntity sceneEntity = assembleSceneEntity(basicInfo.getSceneId(), basicInfo.getType(), basicInfo.getName(),
            basicInfo.getScriptType(), config, feature);
        // 执行数据库操作
        sceneManageMapper.insert(sceneEntity);
        // 回填自增主键
        long sceneId = sceneEntity.getId();
        log.info("创建了业务活动「{}」。自增主键：{}.", basicInfo.getName(), sceneId);
        return sceneId;
    }

    /**
     * 更新压测场景 - 步骤1 : 基础信息
     *
     * @param basicInfo      基础信息
     *                       <p>场景名称</p>
     *                       <p>场景类型
     *                       <ul>
     *                           <li>0:普通场景</li>
     *                           <li>1:流量调试场景</li>
     *                       </ul>
     *                       </p>
     *                       <p>
     *                       脚本类型
     *                       <ul>
     *                           <li>0:Jmeter</li>
     *                           <li>1:Gatling</li>
     *                       </ul>
     *                       </p>
     *                       <p>脚本实例主键</p>
     *                       <p>业务流程主键</p>
     * @param config         施压配置
     * @param analysisResult 脚本解析结果
     * @param dataValidation 数据验证配置
     * @return 数据库更新行数 - 应当为 1
     */
    private int updateStepScene(BasicInfo basicInfo,
        Config config, List<?> analysisResult, DataValidation dataValidation) {
        Map<String, Object> feature = assembleFeature(basicInfo.getScriptId(), basicInfo.getBusinessFlowId(), analysisResult, dataValidation);
        // 组装数据实体类
        SceneManageEntity sceneEntity = assembleSceneEntity(basicInfo.getSceneId(), basicInfo.getType(), basicInfo.getName(),
            basicInfo.getScriptType(), config, feature);
        // 设置更新时间
        sceneEntity.setUpdateTime(new Date());
        // 执行数据库操作
        int updateRows = sceneManageMapper.updateById(sceneEntity);
        log.info("更新了业务活动「{}」。自增主键：{}。操作行数：{}。", basicInfo.getName(), sceneEntity.getId(), updateRows);
        return updateRows;
    }

    /**
     * 创建/更新 压测场景 - 步骤2 : 关联业务活动
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
     * 创建/更新 压测场景 - 步骤3 : 关联压测文件
     *
     * @param sceneId    场景主键
     * @param scriptId   脚本主键
     * @param scriptType 脚本类型
     * @param file       压测文件
     */
    private void createStepScript(long sceneId, Long scriptId, Integer scriptType, List<File> file) {
        List<SceneScriptRefEntity> sceneScriptRefEntityList = new ArrayList<>(file.size());
        for (File t : file) {
            String fileName = t.getName();
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
     * 创建/更新 压测场景 - 步骤4 : 关联SLA
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

    /**
     * 组装拓展字段
     *
     * @param scriptId       脚本主键
     * @param businessFlowId 业务流程主键
     * @param analysisResult 脚本解析结果
     * @param dataValidation 数据验证配置
     * @return 拓展字段的JSON对象
     */
    private Map<String, Object> assembleFeature(long scriptId, long businessFlowId, Object analysisResult, DataValidation dataValidation) {
        return new HashMap<String, Object>(4) {{
            put("scriptId", scriptId);
            put("analysisResult", analysisResult);
            put("businessFlowId", businessFlowId);
            put("dataValidation", dataValidation);
        }};
    }

    /**
     * 组装场景实体类
     *
     * @param sceneId    场景主键
     * @param type       场景类型
     * @param name       场景名称
     * @param scriptType 脚本类型
     * @param feature    拓展字段
     * @param config     施压线程组配置
     * @return 场景实体类
     */
    private SceneManageEntity assembleSceneEntity(Long sceneId, int type, String name, int scriptType, Config config, Object feature) {
        return new SceneManageEntity() {{
            setId(sceneId);
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
    }
}
