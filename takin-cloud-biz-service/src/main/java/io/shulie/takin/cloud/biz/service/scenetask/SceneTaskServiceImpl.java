package io.shulie.takin.cloud.biz.service.scenetask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.pamirs.takin.entity.dao.report.TReportBusinessActivityDetailMapper;
import com.pamirs.takin.entity.dao.report.TReportMapper;
import com.pamirs.takin.entity.dao.scene.manage.TSceneManageMapper;
import com.pamirs.takin.entity.domain.entity.report.Report;
import com.pamirs.takin.entity.domain.entity.report.ReportBusinessActivityDetail;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneFileReadPosition;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneManage;
import com.pamirs.takin.entity.domain.vo.file.FileSliceRequest;
import com.pamirs.takin.entity.domain.vo.report.SceneTaskNotifyParam;
import io.shulie.takin.cloud.biz.collector.collector.CollectorService;
import io.shulie.takin.cloud.biz.input.scenemanage.EnginePluginInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneInspectInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageWrapperInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneSlaRefInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneStartTrialRunInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskQueryTpsInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartCheckInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartCheckInput.FileInfo;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskUpdateTpsInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTryRunInput;
import io.shulie.takin.cloud.biz.output.report.SceneInspectTaskStartOutput;
import io.shulie.takin.cloud.biz.output.report.SceneInspectTaskStopOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneScriptRefOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneActionOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneJobStateOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTaskQueryTpsOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTaskStartCheckOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTaskStartCheckOutput.FileReadInfo;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTryRunTaskStartOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTryRunTaskStatusOutput;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskEventServie;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskService;
import io.shulie.takin.cloud.biz.service.schedule.FileSliceService;
import io.shulie.takin.cloud.common.bean.RuleBean;
import io.shulie.takin.cloud.common.bean.TimeBean;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneBusinessActivityRefBean;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOpitons;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.constants.Constants;
import io.shulie.takin.cloud.common.constants.PressureInstanceRedisKey;
import io.shulie.takin.cloud.common.constants.ReportConstans;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.common.constants.SceneStartCheckConstants;
import io.shulie.takin.cloud.common.constants.SceneTaskRedisConstants;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.PressureTypeEnums;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneRunTaskStatusEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneStopReasonEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import io.shulie.takin.cloud.common.utils.EnginePluginUtils;
import io.shulie.takin.cloud.common.utils.FileSliceByPodNum.StartEndPair;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.sceneTask.SceneTaskPressureTestLogUploadDAO;
import io.shulie.takin.cloud.data.dao.scenemanage.SceneManageDAO;
import io.shulie.takin.cloud.data.model.mysql.SceneBigFileSliceEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageResult;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageListResult;
import io.shulie.takin.ext.api.AssetExtApi;
import io.shulie.takin.ext.api.EngineCallExtApi;
import io.shulie.takin.ext.content.asset.AccountInfoExt;
import io.shulie.takin.ext.content.asset.AssetInvoiceExt;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.utils.json.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 莫问
 * @date 2020-04-22
 */
@Service
@Slf4j
public class SceneTaskServiceImpl implements SceneTaskService {

    @Resource
    private TSceneManageMapper tSceneManageMapper;

    @Autowired
    private SceneManageService sceneManageService;

    @Autowired
    private SceneTaskEventServie sceneTaskEventServie;

    @Resource
    private TReportMapper tReportMapper;
    // 初始化报告开始时间偏移时间
    @Value("${init.report.startTime.Offset:10}")
    private Long offsetStartTime;

    @Resource
    private TReportBusinessActivityDetailMapper tReportBusinessActivityDetailMapper;

    @Autowired
    private RedisClientUtils redisClientUtils;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SceneManageDAO sceneManageDao;

    @Autowired
    private SceneTaskPressureTestLogUploadDAO sceneTaskPressureTestLogUploadDao;

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private PluginManager pluginManager;

    @Autowired
    private FileSliceService fileSliceService;

    @Autowired
    private SceneManageDAO sceneManageDAO;

    @Autowired
    private EnginePluginUtils enginePluginUtils;

    private static final Long KB = 1024L;
    private static final Long MB = KB * 1024;
    private static final Long GB = MB * 1024;
    private static final Long TB = GB * 1024;

    @Override
    @Transactional
    public SceneActionOutput start(SceneTaskStartInput input) {
        CloudPluginUtils.fillUserData(input);
        return startTask(input, null);
    }

    private SceneActionOutput startTask(SceneTaskStartInput input, SceneStartTrialRunInput trialRunInput) {

        SceneManageQueryOpitons options = new SceneManageQueryOpitons();
        options.setIncludeBusinessActivity(true);
        options.setIncludeScript(true);
        SceneManageWrapperOutput sceneData = sceneManageService.getSceneManage(input.getSceneId(), options);

        if (CollectionUtils.isNotEmpty(input.getEnginePlugins())) {
            sceneData.setEnginePlugins(input.getEnginePlugins()
                .stream()
                .filter(Objects::nonNull)
                .map(plugin -> SceneManageWrapperOutput.EnginePluginRefOutput.create(plugin.getPluginId(), plugin.getPluginVersion()))
                .collect(Collectors.toList()));
        } else {
            sceneData.setEnginePlugins(null);
        }
        //end

        if (trialRunInput != null) {
            sceneData.setPressureTestSecond(trialRunInput.getPressureTestSecond());
        }

        //设置巡检参数
        if (input.getSceneInspectInput() != null) {
            SceneInspectInput inspectInput = input.getSceneInspectInput();
            sceneData.setLoopsNum(inspectInput.getLoopsNum());
            sceneData.setFixedTimer(inspectInput.getFixedTimer());
            sceneData.setInspect(true);
        }
        // 设置脚本试跑参数
        if (Objects.nonNull(input.getSceneTryRunInput())) {
            SceneTryRunInput sceneTryRunInput = input.getSceneTryRunInput();
            sceneData.setLoopsNum(sceneTryRunInput.getLoopsNum());
            // 传入并发数
            sceneData.setConcurrenceNum(sceneTryRunInput.getConcurrencyNum());
            sceneData.setTryRun(true);
        }
        //缓存本次压测使用的脚本ID，在记录文件读取位点的时候使用
        if (Objects.isNull(input.getSceneInspectInput()) && Objects.isNull(input.getSceneTryRunInput())) {
            SceneManageEntity sceneManageEntity = sceneManageDAO.queueSceneById(input.getSceneId());
            if (Objects.nonNull(sceneManageEntity)) {
                JSONObject features = JSONObject.parseObject(sceneManageEntity.getFeatures());
                Long scriptId = features.getLong("scriptId");
                redisClientUtils.hmset(String.format(SceneStartCheckConstants.SCENE_KEY, input.getSceneId()), SceneStartCheckConstants.SCRIPT_ID_KEY, scriptId);
            }
        }
        //文件是否继续读取
        sceneData.setContinueRead(input.getContinueRead());

        //启动前置校验
        preCheckStart(sceneData);

        //创建临时报表数据
        Report report = initReport(sceneData, input);

        SceneActionOutput sceneAction = new SceneActionOutput();
        sceneAction.setData(report.getId());
        // 报告已经完成，则退出
        if (report.getStatus() == ReportConstans.FINISH_STATUS) {
            //失败状态
            JSONObject jb = JSON.parseObject(report.getFeatures());
            sceneAction.setMsg(Arrays.asList(jb.getString(ReportConstans.PRESSURE_MSG).split(",")));
            return sceneAction;
        }

        //冻结流量
        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (assetExtApi != null) {
            assetExtApi.lock(new AssetInvoiceExt() {{
                setExpectThroughput(sceneData.getConcurrenceNum());
                setIncreasingTime(sceneData.getIncreasingSecond());
                setPressureMode(sceneData.getPressureMode());
                setPressureTotalTime(sceneData.getTotalTestTime());
                setSceneId(input.getSceneId());
                setTaskId(report.getId());
                setPressureType(sceneData.getPressureType());
                setCustomerId(sceneData.getCustomerId());
                setStep(sceneData.getStep());
            }});
        }

        //设置缓存，用以检查压测场景启动状态 lxr 20210623
        String key = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", input.getSceneId(), report.getId());
        redisClientUtils.hmset(key, SceneTaskRedisConstants.SCENE_RUN_TASK_STATUS_KEY, SceneRunTaskStatusEnum.STARTING.getText());
        //缓存pod数量，上传jmeter日志时判断是否所有文件都上传完成
        redisClientUtils.hmset(ScheduleConstants.SCHEDULE_POD_NUM, String.valueOf(input.getSceneId()), sceneData.getIpNum());
        String engineInstanceRedisKey = PressureInstanceRedisKey.getEngineInstanceRedisKey(input.getSceneId(), report.getId(), input.getCustomerId());
        List<String> activityRefs = sceneData.getBusinessActivityConfig().stream().map(SceneManageWrapperOutput.SceneBusinessActivityRefOutput::getBindRef)
            .collect(Collectors.toList());
        redisClientUtils.hmset(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.ACTIVITY_REFS
            , JsonHelper.bean2Json(activityRefs));
        //广播事件
        sceneTaskEventServie.callStartEvent(sceneData, report.getId());

        return sceneAction;
    }

    @Override
    public void stop(Long sceneId) {
        SceneManageResult sceneManage = sceneManageDAO.getSceneById(sceneId);
        if (sceneManage == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, "压测场景不存在");
        }
        if (SceneManageStatusEnum.ifStop(sceneManage.getStatus())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, "场景状态不为压测中");
        }
        ReportResult reportResult = reportDao.getReportBySceneId(sceneId);

        if (reportResult != null) {
            sceneTaskEventServie.callStopEvent(reportResult);
        }
    }

    @Override
    public SceneActionOutput checkSceneTaskStatus(Long sceneId, Long reportId) {
        SceneActionOutput scene = new SceneActionOutput();
        SceneManageResult sceneManage = sceneManageDAO.getSceneById(sceneId);
        if (sceneManage != null) {
            // 监测启动状态
            scene.setData(SceneManageStatusEnum.getAdaptStatus(sceneManage.getStatus()).longValue());
            if (sceneManage.getStatus() >= 0) {
                ReportResult reportResult;
                if (reportId == null) {
                    //report = TReportMapper.getReportBySceneId(sceneId);
                    reportResult = reportDao.getReportBySceneId(sceneId);
                } else {
                    reportResult = reportDao.selectById(reportId);
                }
                if (reportResult != null) {
                    // 记录错误信息
                    List<String> errorMsgs = Lists.newArrayList();
                    // 检查压测引擎返回内容
                    String key = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneId, reportResult.getId());
                    Object errorObj = redisClientUtils.hmget(key, SceneTaskRedisConstants.SCENE_RUN_TASK_ERROR);
                    if (Objects.nonNull(errorObj) && !Constants.NULL_SIGN.equals(errorObj)) {
                        errorMsgs.add(SceneStopReasonEnum.ENGINE.getType() + ":" + errorObj);
                    }
                    scene.setReportId(reportResult.getId());
                    if (StringUtils.isNotEmpty(reportResult.getFeatures())) {
                        JSONObject jb = JSON.parseObject(reportResult.getFeatures());
                        errorMsgs.add(jb.getString(ReportConstans.FEATURES_ERROR_MSG));
                    }
                    if (CollectionUtils.isNotEmpty(errorMsgs)) {
                        scene.setMsg(errorMsgs);
                        //  前端只有等于0,才会显示错误
                        scene.setData(0L);
                    }
                }
            }
        }
        return scene;
    }

    /**
     * 报告生成触发条件，metric数据完全上传至influxdb 才触发
     * 可以查看 finished 事件
     */
    @Override
    public void handleSceneTaskEvent(TaskResult taskResult) {
        if (taskResult != null && taskResult.getStatus() != null) {
            switch (taskResult.getStatus()) {
                case FAILED:
                    //启动失败
                    testFailed(taskResult);
                    break;
                case STARTED:
                    testStarted(taskResult);
                    break;
                default:
                    log.warn("其他状态暂时无需处理");
                    break;
            }
        }
    }

    @Override
    public String taskResultNotify(SceneTaskNotifyParam param) {
        return sceneTaskEventServie.callStartResultEvent(param);
    }

    @Override
    public void updateSceneTaskTps(SceneTaskUpdateTpsInput input) {
        CloudPluginUtils.fillUserData(input);
        String engineInstanceRedisKey = PressureInstanceRedisKey.getEngineInstanceRedisKey(input.getSceneId(), input.getReportId(),
            input.getCustomerId());
        Object totalIp = redisTemplate.opsForHash().get(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_POD_NUM);
        if (totalIp == null) {
            log.error("异常代码【{}】,异常内容：更新运行任务tps，获取不到pod总数 --> 异常信息:SceneId:{},ReportId:{}, CustomerId:{}",
                TakinCloudExceptionEnum.TASK_START_ERROR_CHECK_POD, input.getSceneId(), input.getReportId(),
                input.getCustomerId());
            return;
        }
        BigDecimal podTpsNum = new BigDecimal(input.getTpsNum()).divide(new BigDecimal(totalIp.toString()), 0, RoundingMode.UP);
        redisTemplate.opsForHash().put(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_ALL_LIMIT, input.getTpsNum() + "");
        redisTemplate.opsForHash().put(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_LIMIT, podTpsNum + "");
    }

    @Override
    public SceneTaskQueryTpsOutput queryAdjustTaskTps(SceneTaskQueryTpsInput input) {
        CloudPluginUtils.fillUserData(input);
        String engineInstanceRedisKey = PressureInstanceRedisKey.getEngineInstanceRedisKey(input.getSceneId(), input.getReportId(),
            input.getCustomerId());
        Object object = redisTemplate.opsForHash().get(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_ALL_LIMIT);

        SceneTaskQueryTpsOutput sceneTaskQueryTpsOutput = new SceneTaskQueryTpsOutput();
        if (object != null) {
            sceneTaskQueryTpsOutput.setTotalTps(Long.parseLong(object.toString()));
            return sceneTaskQueryTpsOutput;
        }
        return null;
    }

    @Override
    public Long startFlowDebugTask(SceneManageWrapperInput input, List<EnginePluginInput> enginePlugins) {

        Long sceneManageId;
        CloudPluginUtils.fillUserData(input);
        //首先根据脚本实例id构建压测场景名称
        String pressureTestSceneName = SceneManageConstant.SCENE_MANAGER_FLOW_DEBUG + input.getCustomerId() + "_" + input.getScriptId();

        //根据场景名称查询是否已经存在场景
        SceneManageListResult sceneManageResult = sceneManageDao.queryBySceneName(pressureTestSceneName);

        //不存在，新增压测场景
        if (sceneManageResult == null) {

            input.setPressureTestSceneName(pressureTestSceneName);
            input.setPressureType(PressureTypeEnums.FLOW_DEBUG.getCode());
            // 后续会根据传入并发数进行修改
            input.setConcurrenceNum(1);
            input.setIpNum(1);
            input.setPressureTestTime(new TimeBean(30L, "m"));
            input.setPressureMode(0);
            input.setType(1);

            SceneSlaRefInput sceneSlaRefInput = new SceneSlaRefInput();
            sceneSlaRefInput.setRuleName("FLOW_DEBUG_SLA");
            sceneSlaRefInput.setBusinessActivity(new String[] {"-1"});
            RuleBean ruleBean = new RuleBean();
            ruleBean.setIndexInfo(0);
            ruleBean.setCondition(0);
            ruleBean.setDuring(new BigDecimal("10000"));
            ruleBean.setTimes(100);
            sceneSlaRefInput.setRule(ruleBean);
            sceneSlaRefInput.setStatus(0);
            input.setStopCondition(Collections.singletonList(sceneSlaRefInput));
            input.setWarningCondition(new ArrayList<>(0));
            sceneManageId = sceneManageService.addSceneManage(input);

        } else {
            sceneManageId = sceneManageResult.getId();
        }

        //启动该压测场景
        SceneTaskStartInput sceneTaskStartInput = new SceneTaskStartInput();
        sceneTaskStartInput.setSceneId(sceneManageId);
        sceneTaskStartInput.setUserId(input.getUserId());
        //sceneTaskStartInput.setEnginePluginIds(enginePluginIds);
        sceneTaskStartInput.setEnginePlugins(enginePlugins);
        sceneTaskStartInput.setContinueRead(false);
        SceneActionOutput sceneActionDTO = startTask(sceneTaskStartInput, null);
        //返回报告id
        return sceneActionDTO.getData();
    }

    @Override
    public SceneInspectTaskStartOutput startInspectTask(SceneManageWrapperInput input, List<EnginePluginInput> enginePlugins) {
        CloudPluginUtils.fillUserData(input);
        SceneInspectTaskStartOutput startOutput = new SceneInspectTaskStartOutput();
        Long sceneManageId = null;
        //首先根据脚本实例id构建压测场景名称
        String pressureTestSceneName = SceneManageConstant.SCENE_MANAGER_INSPECT + input.getCustomerId() + "_" + input.getScriptId();

        //根据场景名称查询是否已经存在场景
        SceneManageListResult sceneManageResult = sceneManageDao.queryBySceneName(pressureTestSceneName);

        //不存在，新增压测场景
        if (sceneManageResult == null) {
            input.setPressureTestSceneName(pressureTestSceneName);
            input.setPressureType(PressureTypeEnums.INSPECTION_MODE.getCode());
            input.setConcurrenceNum(1);
            input.setIpNum(1);
            input.setPressureTestTime(new TimeBean(365L, "d"));
            input.setPressureMode(0);
            input.setType(1);
            SceneSlaRefInput sceneSlaRefInput = new SceneSlaRefInput();
            sceneSlaRefInput.setRuleName("INSPECT_SLA");
            sceneSlaRefInput.setBusinessActivity(new String[] {"-2"});
            RuleBean ruleBean = new RuleBean();
            ruleBean.setIndexInfo(0);
            ruleBean.setCondition(0);
            ruleBean.setDuring(new BigDecimal("10000"));
            ruleBean.setTimes(100);
            sceneSlaRefInput.setRule(ruleBean);
            sceneSlaRefInput.setStatus(0);
            input.setStopCondition(Collections.singletonList(sceneSlaRefInput));
            input.setWarningCondition(new ArrayList<>(0));
            sceneManageId = sceneManageService.addSceneManage(input);
        } else {
            SceneManageStatusEnum statusEnum = SceneManageStatusEnum.getSceneManageStatusEnum(
                sceneManageResult.getStatus());
            if (!SceneManageStatusEnum.getFree().contains(statusEnum)) {
                String errMsg = "启动巡检场景失败，场景前置状态校验失败:" + statusEnum.getDesc();
                log.error("异常代码【{}】,异常内容：启动巡检场景失败 --> 场景前置状态校验失败: {}",
                    TakinCloudExceptionEnum.INSPECT_TASK_START_ERROR, statusEnum.getDesc());
                startOutput.setSceneId(sceneManageId);
                startOutput.setMsg(Collections.singletonList(errMsg));
                return startOutput;
            }
            sceneManageId = sceneManageResult.getId();
        }

        //启动该压测场景
        SceneTaskStartInput sceneTaskStartInput = new SceneTaskStartInput();
        sceneTaskStartInput.setSceneId(sceneManageId);
        //sceneTaskStartInput.setEnginePluginIds(enginePluginIds);
        sceneTaskStartInput.setEnginePlugins(enginePlugins);
        Long fixTimer = input.getFixTimer();
        Integer loopsNum = input.getLoopsNum();
        SceneInspectInput inspectInput = new SceneInspectInput().setFixedTimer(fixTimer).setLoopsNum(loopsNum);
        sceneTaskStartInput.setSceneInspectInput(inspectInput);
        sceneTaskStartInput.setContinueRead(false);
        SceneActionOutput sceneActionOutput = startTask(sceneTaskStartInput, null);
        startOutput.setSceneId(sceneManageId);
        startOutput.setReportId(sceneActionOutput.getData());
        //开始试跑就设置一个状态，后面区分试跑任务和正常压测
        String key = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneManageId, sceneActionOutput.getData());
        redisClientUtils.hmset(key, SceneTaskRedisConstants.SCENE_RUN_TASK_STATUS_KEY, SceneRunTaskStatusEnum.STARTING.getText());
        return startOutput;
    }

    @Override
    public SceneInspectTaskStopOutput stopInspectTask(Long sceneId) {
        SceneInspectTaskStopOutput output = new SceneInspectTaskStopOutput();
        output.setSceneId(sceneId);
        SceneManageResult sceneManage = sceneManageDAO.getSceneById(sceneId);
        if (!Objects.isNull(sceneManage)) {
            SceneManageStatusEnum statusEnum = SceneManageStatusEnum.getSceneManageStatusEnum(sceneManage.getStatus());
            if (!SceneManageStatusEnum.getWorking().contains(statusEnum) && !SceneManageStatusEnum.getFree().contains(
                statusEnum)) {
                String errMsg = "停止巡检场景失败，场景前置状态校验失败:" + statusEnum.getDesc();
                log.error(errMsg);
                output.setSceneId(sceneManage.getId());
                output.setMsg(Collections.singletonList(errMsg));
                return output;
            } else {
                log.info("任务{} ，原因：巡检场景触发停止", sceneId);
                stop(sceneId);
            }
        } else {
            String errMsg = "场景不存在:[" + sceneId + "]";
            output.setMsg(Collections.singletonList(errMsg));
        }
        return output;
    }

    @Override
    public SceneTryRunTaskStartOutput startTryRun(SceneManageWrapperInput input, List<EnginePluginInput> enginePlugins) {
        Long sceneManageId;
        CloudPluginUtils.fillUserData(input);
        //首先根据脚本实例id构建压测场景名称
        String pressureTestSceneName = SceneManageConstant.SCENE_MANAGER_TRY_RUN + input.getCustomerId() + "_" + input
            .getScriptId();
        //根据场景名称查询是否已经存在场景
        SceneManageListResult sceneManageResult = sceneManageDao.queryBySceneName(pressureTestSceneName);
        SceneTryRunTaskStartOutput sceneTryRunTaskStartOutput = new SceneTryRunTaskStartOutput();
        CloudPluginUtils.fillUserData(sceneTryRunTaskStartOutput);
        //不存在，新增压测场景
        if (sceneManageResult == null) {

            input.setPressureTestSceneName(pressureTestSceneName);
            input.setPressureType(PressureTypeEnums.TRY_RUN.getCode());
            input.setConcurrenceNum(1);
            input.setIpNum(1);
            input.setPressureTestTime(new TimeBean(30L, "m"));
            input.setPressureMode(0);

            input.setType(1);
            SceneSlaRefInput sceneSlaRefInput = new SceneSlaRefInput();
            sceneSlaRefInput.setRuleName("TRY_RUN_SLA");
            sceneSlaRefInput.setBusinessActivity(new String[] {"-1"});
            RuleBean ruleBean = new RuleBean();
            ruleBean.setIndexInfo(0);
            ruleBean.setCondition(0);
            ruleBean.setDuring(new BigDecimal("10000"));
            ruleBean.setTimes(100);
            sceneSlaRefInput.setRule(ruleBean);
            sceneSlaRefInput.setStatus(0);
            input.setStopCondition(Collections.singletonList(sceneSlaRefInput));
            input.setWarningCondition(new ArrayList<>(0));
            sceneManageId = sceneManageService.addSceneManage(input);
        } else {
            sceneManageId = sceneManageResult.getId();
        }
        sceneTryRunTaskStartOutput.setSceneId(sceneManageId);
        //启动该压测场景
        SceneTaskStartInput sceneTaskStartInput = new SceneTaskStartInput();
        sceneTaskStartInput.setSceneId(sceneManageId);
        //sceneTaskStartInput.setEnginePluginIds(enginePluginIds);
        sceneTaskStartInput.setEnginePlugins(enginePlugins);
        sceneTaskStartInput.setContinueRead(false);
        //TODO 根据次数，设置时间
        SceneTryRunInput tryRunInput = new SceneTryRunInput(input.getLoopsNum(), input.getConcurrencyNum());
        sceneTaskStartInput.setSceneTryRunInput(tryRunInput);
        SceneActionOutput sceneActionOutput = startTask(sceneTaskStartInput, null);
        sceneTryRunTaskStartOutput.setReportId(sceneActionOutput.getData());

        return sceneTryRunTaskStartOutput;
    }


    @Override
    public SceneTryRunTaskStatusOutput checkTaskStatus(Long sceneId, Long reportId) {
        String key = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneId, reportId);
        Object status = redisClientUtils.hmget(key, SceneTaskRedisConstants.SCENE_RUN_TASK_STATUS_KEY);
        SceneTryRunTaskStatusOutput output = new SceneTryRunTaskStatusOutput();
        if (Objects.nonNull(status)) {
            SceneRunTaskStatusEnum statusEnum = SceneRunTaskStatusEnum.getTryRunTaskStatusEnumByText(status.toString());
            if (Objects.isNull(statusEnum)) {
                output.setTaskStatus(SceneRunTaskStatusEnum.STARTING.getCode());
                return output;
            }
            output.setTaskStatus(statusEnum.getCode());
            if (statusEnum.equals(SceneRunTaskStatusEnum.FAILED)) {
                Object errorObj = redisClientUtils.hmget(key, SceneTaskRedisConstants.SCENE_RUN_TASK_ERROR);
                if (Objects.nonNull(errorObj)) {
                    output.setErrorMsg(errorObj.toString());
                }
            }
        } else {
            output.setTaskStatus(SceneRunTaskStatusEnum.STARTING.getCode());
        }
        return output;
    }

    @Override
    public SceneJobStateOutput checkSceneJobStatus(Long sceneId) {
        SceneJobStateOutput state = new SceneJobStateOutput();
        SceneManageResult sceneManage = sceneManageDAO.getSceneById(sceneId);
        if (Objects.isNull(sceneManage)) {
            state.setState(SceneManageConstant.SCENETASK_JOB_STATUS_NONE);
            state.setMsg("未查询到相应的压测场景");
            return state;
        }
        //获取报告ID
        String reportId = "";
        if (sceneManage.getStatus() >= 1) {
            Report report = tReportMapper.getReportBySceneId(sceneId);
            if (report != null) {
                reportId = report.getId().toString();
            }
        } else {
            state.setState(SceneManageConstant.SCENETASK_JOB_STATUS_NONE);
            state.setMsg("压测任务未启动");
            return state;
        }
        if (StringUtils.isEmpty(reportId)) {
            state.setState(SceneManageConstant.SCENETASK_JOB_STATUS_NONE);
            state.setMsg("未获取到相应压测报告");
            return state;
        }
        String jobName = ScheduleConstants.getScheduleName(sceneId, Long.parseLong(reportId), sceneManage.getCustomerId());
        EngineCallExtApi engineCallExtApi = enginePluginUtils.getEngineCallExtApi();
        String status = engineCallExtApi.getJobStatus(jobName);
        state.setState(status);

        if (Objects.equals(status, SceneManageConstant.SCENETASK_JOB_STATUS_RUNNING)) {
            state.setMsg("任务执行中");
        } else if (Objects.equals(status, SceneManageConstant.SCENETASK_JOB_STATUS_NONE)) {
            state.setMsg("任务已停止");
        } else {
            state.setMsg("任务执行错误");
        }
        return state;
    }

    /**
     * 场景启动前置校验
     */
    private void preCheckStart(SceneManageWrapperOutput sceneData) {
        // 流量判断
        {
            if (sceneData.getCustomerId() == null) {
                throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "场景没有绑定客户信息");
            }
            AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
            if (assetExtApi != null) {
                List<AccountInfoExt> accountInfoList = assetExtApi.queryAccountInfoByUserIds(
                    new ArrayList<Long>(1) {{
                        add(sceneData.getCustomerId());
                    }});
                if (accountInfoList != null && accountInfoList.size() >= 1) {
                    if (accountInfoList.get(0).getBalance().compareTo(sceneData.getEstimateFlow()) < 0) {
                        throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "压测流量不足！");
                    }
                }
            }
        }

        if (!SceneManageStatusEnum.ifFree(sceneData.getStatus())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "当前场景不为待启动状态！");
        }

        // 判断场景是否有job正在执行 一个场景只能保证一个job执行
        //获取所有job
        EngineCallExtApi engineCallExtApi = enginePluginUtils.getEngineCallExtApi();
        List<String> allRunningJobName = engineCallExtApi.getAllRunningJobName();
        if (CollectionUtils.isNotEmpty(allRunningJobName)) {
            //获取其中属于我们的压测任务
            List<Long> sceneTaskJobNames = allRunningJobName.stream().filter(jobName -> jobName.startsWith(ScheduleConstants.SCENE_TASK))
                .map(jobName -> {
                    String tempString = jobName.replace(ScheduleConstants.SCENE_TASK, "");
                    String substring = tempString.substring(0, tempString.indexOf("-"));
                    return Long.parseLong(substring);
                }).collect(Collectors.toList());
            log.info("获取到正在运行的job:{}", JsonHelper.bean2Json(sceneTaskJobNames));
            if (sceneTaskJobNames.contains(sceneData.getId())) {
                throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "场景【" + sceneData.getId() + "】"
                    + "存在未删除的job,请等待删除或者人为判断是否可以手工删除~");
            }

        }
    }

    /**
     * 初始化报表
     *
     * @return -
     */
    public Report initReport(SceneManageWrapperOutput scene, SceneTaskStartInput input) {
        Report report = new Report();
        report.setSceneId(scene.getId());
        report.setConcurrent(scene.getConcurrenceNum());
        report.setStatus(ReportConstans.INIT_STATUS);
        // 初始化
        report.setCustomerId(scene.getCustomerId());
        report.setOperateId(input.getUserId());
        report.setDeptId(input.getDeptId());
        // 解决开始时间 偏移10s
        report.setStartTime(new Date(System.currentTimeMillis() + offsetStartTime * 1000));
        //负责人默认启动人
        report.setUserId(input.getUserId());
        report.setSceneName(scene.getPressureTestSceneName());

        if (scene.getFeatures() != null) {
            Map<String, String> map = JsonHelper.string2Obj(scene.getFeatures(),
                new TypeReference<Map<String, String>>() {
                });
            if (map != null && map.get(SceneManageConstant.FEATURES_SCRIPT_ID) != null) {
                report.setScriptId(Long.valueOf(map.get(SceneManageConstant.FEATURES_SCRIPT_ID)));
            }
        }
        int sumTps = scene.getBusinessActivityConfig().stream().mapToInt(SceneBusinessActivityRefBean::getTargetTPS).sum();
        report.setTps(sumTps);
        report.setPressureType(scene.getPressureType());
        report.setType(scene.getType());
        tReportMapper.insertSelective(report);

        //标记场景
        // 待启动,压测失败，停止压测（压测工作已停止） 强制停止 ---> 启动中
        Boolean updateFlag = sceneManageService.updateSceneLifeCycle(
            UpdateStatusBean.build(scene.getId(), report.getId(), scene.getCustomerId())
                .checkEnum(SceneManageStatusEnum.WAIT, SceneManageStatusEnum.FAILED, SceneManageStatusEnum.STOP, SceneManageStatusEnum.FORCE_STOP)
                .updateEnum(SceneManageStatusEnum.STARTING).build());
        if (!updateFlag) {
            //失败状态 获取最新的报告
            report = tReportMapper.selectByPrimaryKey(report.getId());
            return report;
        }
        Long reportId = report.getId();
        //初始化业务活动
        scene.getBusinessActivityConfig().forEach(activity -> {
            ReportBusinessActivityDetail reportBusinessActivityDetail = new ReportBusinessActivityDetail();
            reportBusinessActivityDetail.setReportId(reportId);
            reportBusinessActivityDetail.setSceneId(scene.getId());
            reportBusinessActivityDetail.setBusinessActivityId(activity.getBusinessActivityId());
            reportBusinessActivityDetail.setBusinessActivityName(activity.getBusinessActivityName());
            reportBusinessActivityDetail.setApplicationIds(activity.getApplicationIds());
            reportBusinessActivityDetail.setBindRef(activity.getBindRef());
            reportBusinessActivityDetail.setTargetTps(new BigDecimal(activity.getTargetTPS()));
            reportBusinessActivityDetail.setTargetRt(new BigDecimal(activity.getTargetRT()));
            reportBusinessActivityDetail.setTargetSuccessRate(activity.getTargetSuccessRate());
            reportBusinessActivityDetail.setTargetSa(activity.getTargetSA());
            tReportBusinessActivityDetailMapper.insertSelective(reportBusinessActivityDetail);
        });

        log.info("启动[{}]场景测试，初始化报表数据,报表ID: {}", scene.getId(), report.getId());
        return report;
    }

    /**
     * 压测任务正常开启 这里实际是 压力节点 启动成功
     * 20200923 报告 开始时间 记录在redis 中
     *
     * @see CollectorService
     **/
    @Transactional(rollbackFor = Exception.class)
    public synchronized void testStarted(TaskResult taskResult) {
        log.info("场景[{}-{}-{}]启动新的压测任务", taskResult.getSceneId(), taskResult.getTaskId(), taskResult.getCustomerId());
        //场景压测进行中
        // job创建中 改成 pod工作中 隐式 状态严格 更新 解决 多pod 问题
        // 进行计数
        String pressureNodeName = ScheduleConstants.getPressureNodeName(taskResult.getSceneId(), taskResult.getTaskId(),
            taskResult.getCustomerId());

        // cloud集群 redis同步操作，increment 直接拿数据，无需重新获取key的value
        long num = redisClientUtils.increment(pressureNodeName, 1);
        log.info("当前启动pod成功数量=【{}】", num);
        if (num == 1) {
            // 启动只更新一次
            sceneManageService.updateSceneLifeCycle(
                UpdateStatusBean.build(taskResult.getSceneId(), taskResult.getTaskId(), taskResult.getCustomerId())
                    .checkEnum(SceneManageStatusEnum.JOB_CREATEING)
                    .updateEnum(SceneManageStatusEnum.PRESSURE_NODE_RUNNING).build());
        }

    }

    /**
     * 压测任务启动失败
     */
    @Transactional
    public void testFailed(TaskResult taskResult) {
        log.info("场景[{}]压测任务启动失败，失败原因:{}", taskResult.getSceneId(), taskResult.getMsg());
        Report report = tReportMapper.selectByPrimaryKey(taskResult.getTaskId());
        if (report != null && report.getStatus() == ReportConstans.INIT_STATUS) {
            //删除报表
            report.setGmtUpdate(new Date());
            report.setIsDeleted(1);
            report.setId(taskResult.getTaskId());
            JSONObject json = new JSONObject();
            json.put(ReportConstans.FEATURES_ERROR_MSG, taskResult.getMsg());
            report.setFeatures(json.toJSONString());
            tReportMapper.updateByPrimaryKeySelective(report);

            //释放流量
            AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
            if (assetExtApi != null) {
                assetExtApi.unlock(report.getCustomerId(), taskResult.getTaskId().toString());
            }
            ReportResult recentlyReport = reportDao.getRecentlyReport(taskResult.getSceneId());
            if (!taskResult.getTaskId().equals(recentlyReport.getId())) {
                log.error("更新压测生命周期，所更新的报告不是压测场景的最新报告,场景id:{},更新的报告id:{},当前最新的报告id:{}",
                    taskResult.getSceneId(), taskResult.getTaskId(), recentlyReport.getId());
                return;
            }

            SceneManage sceneManage = new SceneManage();
            sceneManage.setId(taskResult.getSceneId());
            sceneManage.setUpdateTime(new Date());
            sceneManage.setStatus(SceneManageStatusEnum.WAIT.getValue());
            tSceneManageMapper.updateByPrimaryKeySelective(sceneManage);
        }
    }

    @Override
    public SceneTaskStartCheckOutput sceneStartCsvPositionCheck(SceneTaskStartCheckInput input) {
        //1.查询缓存是否有值
        //2.查询pod数量和文件名,进行比较
        //3.查询缓存值是否小于预分片end
        //4.查询脚本是否变更
        SceneTaskStartCheckOutput output = new SceneTaskStartCheckOutput();
        try{
            SceneManageWrapperOutput sceneManage = sceneManageService.getSceneManage(input.getSceneId(),
                new SceneManageQueryOpitons() {{
                    setIncludeBusinessActivity(false);
                    setIncludeScript(true);
                    setIncludeSLA(false);
                }});
            input.setPodNum(sceneManage.getIpNum());
            long sceneId = input.getSceneId();
            List<SceneScriptRefOutput> uploadFile = sceneManage.getUploadFile();
            if (CollectionUtils.isEmpty(uploadFile)){
                output.setHasUnread(false);
                return output;
            }
            Collection<FileInfo> fileInfoList = uploadFile.stream().filter(file -> file.getFileType() == 1)
                .map(file -> {
                    if (file.getFileName().endsWith(".csv")) {
                        FileInfo info = new FileInfo();
                        info.setFileName(file.getFileName());
                        info.setSplit(file.getIsSplit() != null && file.getIsSplit() == 1);
                        return info;
                    }
                    return null;
                }).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(fileInfoList)) {
                output.setHasUnread(false);
                cleanCachedPosition(sceneId);
                return output;
            }
            //判断脚本是否变更
            JSONObject features = JSONObject.parseObject(sceneManage.getFeatures());
            Boolean scriptChange = compareScript(sceneId, features.getString("scriptId"));
            if (!scriptChange){
                output.setHasUnread(false);
                cleanCachedPosition(sceneId);
                return output;
            }
            if (input.getPodNum() > 0) {
                if (!comparePod(sceneId, input.getPodNum())) {
                    output.setHasUnread(false);
                    cleanCachedPosition(sceneId);
                    return output;
                }
                String key = String.format(SceneStartCheckConstants.SCENE_KEY, sceneId);
                Map<Object, Object> positionMap = redisTemplate.opsForHash().entries(key);
                if (Objects.nonNull(positionMap)) {
                    for (FileInfo info : fileInfoList) {
                        comparePosition(output, sceneId, info.getFileName(), input.getPodNum(), info.isSplit(),
                            positionMap);
                        if (!output.getHasUnread()) {
                            cleanCachedPosition(sceneId);
                            output.setFileReadInfos(new ArrayList<>());
                            return output;
                        }
                    }
                } else {
                    cleanCachedPosition(sceneId);
                    output.setHasUnread(false);
                    output.setFileReadInfos(new ArrayList<>());
                    return output;
                }
            }
            output.setHasUnread(true);
        }catch (Exception e){
            log.error("获取文件读取位点信息失败：场景ID：{}，错误信息:{}",input.getSceneId(),e.getMessage());
            output.setHasUnread(false);
        }
        return output;
    }

    private Boolean compareScript(long sceneId,String scriptId) {
        Object scriptIdObj = redisClientUtils.hmget(String.format(SceneStartCheckConstants.SCENE_KEY, sceneId), SceneStartCheckConstants.SCRIPT_ID_KEY);
        return scriptIdObj != null && scriptId.equals(scriptIdObj.toString());
    }

    private boolean comparePod(long sceneId, int podNum) {
        Object podObj = redisClientUtils.hmget(ScheduleConstants.SCHEDULE_POD_NUM, String.valueOf(sceneId));
        if (Objects.nonNull(podObj)){
            int cachedPodNum = Integer.parseInt(podObj.toString());
            return cachedPodNum == podNum;
        }
        return false;
    }

    private void comparePosition(SceneTaskStartCheckOutput output,long sceneId, String fileName, int podNum,boolean isSplit,
        Map<Object, Object> positionMap) {
        SceneBigFileSliceEntity sliceEntity = fileSliceService.getOneByParam(new FileSliceRequest() {{
            setSceneId(sceneId);
            setFileName(fileName);
        }});
        if (Objects.isNull(sliceEntity) || sliceEntity.getSliceCount() != podNum || StringUtils.isBlank(sliceEntity.getSliceInfo())) {
            output.setHasUnread(false);
            return;
        }

        List<FileReadInfo> fileReadInfos = output.getFileReadInfos();
        if (CollectionUtils.isEmpty(fileReadInfos)){
            fileReadInfos = new ArrayList<>();
        }
        List<StartEndPair> startEndPairs = JSONArray.parseArray(sliceEntity.getSliceInfo(), StartEndPair.class);
        long fileSize = startEndPairs.stream().filter(Objects::nonNull)
            .mapToLong(StartEndPair::getEnd)
            .filter(Objects::nonNull)
            .max()
            .orElse(0L);
        if (fileSize == 0){
            output.setHasUnread(false);
            return;
        }
        //文件拆分，计算已读、文件大小
        if (isSplit){
            long readSize = 0;
            for (int i = 0; i < podNum; i++) {
                StartEndPair pair = startEndPairs.get(i);
                Object o = positionMap.get(String.format(SceneStartCheckConstants.FILE_POD_FIELD_KEY, fileName, i + 1));
                if (Objects.isNull(o)) {
                    output.setHasUnread(false);
                    return;
                }
                SceneFileReadPosition position = JSONUtil.toBean(o.toString(), SceneFileReadPosition.class);
                if (position.getReadPosition() < pair.getStart() || position.getReadPosition() > pair.getEnd()) {
                    output.setHasUnread(false);
                    return;
                }
                readSize += position.getReadPosition() - pair.getStart();
            }
            String fileSizeStr = getPositionSize(fileSize);
            String readSizeStr = getPositionSize(readSize);
            FileReadInfo info = new FileReadInfo(){{
                setFileName(fileName);
                setFileSize(fileSizeStr);
                setReadSize(readSizeStr);
            }};
            fileReadInfos.add(info);
            output.setHasUnread(true);
            output.setFileReadInfos(fileReadInfos);
        }
        //文件不拆分，取所有pod中读的最多的提示
        else {
            FileReadInfo info = new FileReadInfo();
            info.setFileName(fileName);
            long readSize = 0;
            for (Entry<Object,Object> entry : positionMap.entrySet()) {
                if (!SceneStartCheckConstants.SCRIPT_ID_KEY.equals(entry.getKey().toString())
                    && entry.getKey().toString().contains(fileName)) {
                    SceneFileReadPosition readPosition = JSONUtil.toBean(entry.getValue().toString(),
                        SceneFileReadPosition.class);
                    if (readPosition.getReadPosition() > readSize) {
                        readSize = readPosition.getReadPosition();
                    }
                }
            }
            String fileSizeStr = getPositionSize(fileSize);
            String readSizeStr = getPositionSize(readSize);
            info.setReadSize(readSizeStr);
            info.setFileSize(fileSizeStr);
            fileReadInfos.add(info);
            output.setHasUnread(true);
            output.setFileReadInfos(fileReadInfos);
        }
    }

    @Override
    public void cleanCachedPosition(Long sceneId) {
        String key = String.format(SceneStartCheckConstants.SCENE_KEY, sceneId);
        redisClientUtils.del(key);
    }

    private String getPositionSize(Long position) {
        if (position > TB){
            return position / TB + "TB";
        }
        if (position > GB){
            return position / GB + "GB";
        }
        if (position > MB){
            return position / MB + "MB";
        }
        if (position > KB) {
            return position / KB + "KB";
        }
        return position + "B";
    }
}
