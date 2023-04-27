package io.shulie.takin.cloud.biz.service.scenetask;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.pamirs.takin.entity.dao.report.TReportMapper;
import com.pamirs.takin.entity.domain.entity.report.Report;
import com.pamirs.takin.entity.domain.entity.report.ReportBusinessActivityDetail;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneFileReadPosition;
import com.pamirs.takin.entity.domain.vo.file.FileSliceRequest;
import com.pamirs.takin.entity.domain.vo.report.SceneTaskNotifyParam;
import io.shulie.takin.cloud.biz.cache.SceneTaskStatusCache;
import io.shulie.takin.cloud.biz.collector.collector.CollectorService;
import io.shulie.takin.cloud.biz.input.scenemanage.*;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartCheckInput.FileInfo;
import io.shulie.takin.cloud.biz.output.report.SceneInspectTaskStartOutput;
import io.shulie.takin.cloud.biz.output.report.SceneInspectTaskStopOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneBusinessActivityRefOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneScriptRefOutput;
import io.shulie.takin.cloud.biz.output.scenetask.*;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTaskStartCheckOutput.FileReadInfo;
import io.shulie.takin.cloud.biz.service.engine.EngineService;
import io.shulie.takin.cloud.biz.service.report.ReportService;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskEventService;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskService;
import io.shulie.takin.cloud.biz.service.schedule.FileSliceService;
import io.shulie.takin.cloud.biz.service.sla.impl.SlaServiceImpl;
import io.shulie.takin.cloud.biz.utils.DataUtils;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOpitons;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.constants.*;
import io.shulie.takin.cloud.common.enums.PressureModeEnum;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.ThreadGroupTypeEnum;
import io.shulie.takin.cloud.common.enums.TimeUnitEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneRunTaskStatusEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneStopReasonEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.*;
import io.shulie.takin.cloud.common.utils.FileSliceByPodNum.StartEndPair;
import io.shulie.takin.cloud.data.dao.report.ReportBusinessActivityDetailDao;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.data.mapper.mysql.ReportMapper;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneBigFileSliceEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageListResult;
import io.shulie.takin.cloud.ext.api.AssetExtApi;
import io.shulie.takin.cloud.ext.api.EngineCallExtApi;
import io.shulie.takin.cloud.ext.content.asset.AccountInfoExt;
import io.shulie.takin.cloud.ext.content.asset.AssetBalanceExt;
import io.shulie.takin.cloud.ext.content.asset.AssetBillExt;
import io.shulie.takin.cloud.ext.content.asset.AssetInvoiceExt;
import io.shulie.takin.cloud.ext.content.enums.AssetTypeEnum;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.response.Response;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.sdk.model.common.RuleBean;
import io.shulie.takin.cloud.sdk.model.common.TimeBean;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.utils.security.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author 莫问
 * @date 2020-04-22
 */
@Service
@Slf4j
public class SceneTaskServiceImpl implements SceneTaskService {
    @Resource
    private ReportDao reportDao;
    @Resource
    private ReportMapper reportMapper;
    @Resource
    private EngineService engineService;
    @Resource
    private ReportService reportService;
    @Resource
    private PluginManager pluginManager;
    @Resource
    private TReportMapper tReportMapper;
    @Resource
    private SceneManageDAO sceneManageDao;
    @Resource
    private SceneManageDAO sceneManageDAO;
    @Resource
    private FileSliceService fileSliceService;
    @Resource
    private EnginePluginUtils enginePluginUtils;
    @Resource
    private DynamicTpsService dynamicTpsService;
    @Resource
    private SceneTaskStatusCache taskStatusCache;
    @Resource
    private SceneManageService sceneManageService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private SceneTaskEventService sceneTaskEventService;
    @Resource
    private ReportBusinessActivityDetailDao reportBusinessActivityDetailDao;

    /**
     * 初始化报告开始时间偏移时间
     */
    @Value("${init.report.startTime.Offset:10}")
    private Long offsetStartTime;

    /**
     * 压力节点 启动时间超时
     */
    @Value("${pressure.node.start.expireTime: 30}")
    private Integer pressureNodeStartExpireTime;

    private static final Long KB = 1024L;
    private static final Long MB = KB * 1024;
    private static final Long GB = MB * 1024;
    private static final Long TB = GB * 1024;

    private static final String SCRIPT_NAME_SUFFIX = "jmx";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SceneActionOutput start(SceneTaskStartInput input) {
        input.setAssetType(AssetTypeEnum.PRESS_REPORT.getCode());
        input.setResourceId(null);
        return startTask(input);
    }

    private SceneActionOutput startTask(SceneTaskStartInput input) {
        log.info("启动任务接收到入参：{}", JsonUtil.toJson(input));
        SceneManageQueryOpitons options = new SceneManageQueryOpitons();
        options.setIncludeBusinessActivity(true);
        options.setIncludeScript(true);
        SceneManageWrapperOutput sceneData = sceneManageService.getSceneManage(input.getSceneId(), options);

        sceneData.setPressureType(PressureSceneEnum.DEFAULT.getCode());
        if (CollectionUtils.isNotEmpty(input.getEnginePlugins())) {
            sceneData.setEnginePlugins(input.getEnginePlugins()
                .stream()
                .filter(Objects::nonNull)
                .map(plugin -> SceneManageWrapperOutput.EnginePluginRefOutput.create(plugin.getPluginId(), plugin.getPluginVersion()))
                .collect(Collectors.toList()));
        } else {
            sceneData.setEnginePlugins(null);
        }

        //设置巡检参数
        if (Objects.nonNull(input.getSceneInspectInput())) {
            SceneInspectInput inspectInput = input.getSceneInspectInput();
            sceneData.setLoopsNum(inspectInput.getLoopsNum());
            sceneData.setFixedTimer(inspectInput.getFixedTimer());
            sceneData.setInspect(true);
            sceneData.setPressureType(PressureSceneEnum.INSPECTION_MODE.getCode());
        }
        // 设置脚本试跑参数
        if (Objects.nonNull(input.getSceneTryRunInput())) {
            SceneTryRunInput sceneTryRunInput = input.getSceneTryRunInput();
            sceneData.setLoopsNum(sceneTryRunInput.getLoopsNum());
            // 传入并发数
            sceneData.setConcurrenceNum(sceneTryRunInput.getConcurrencyNum());
            sceneData.setTryRun(true);
            sceneData.setPressureType(PressureSceneEnum.TRY_RUN.getCode());
        }
        //缓存本次压测使用的脚本ID，在记录文件读取位点的时候使用
        if (Objects.isNull(input.getSceneInspectInput()) && Objects.isNull(input.getSceneTryRunInput())) {
            SceneManageEntity sceneManageEntity = sceneManageDAO.queueSceneById(input.getSceneId());
            if (Objects.nonNull(sceneManageEntity)) {
                JSONObject features = JsonUtil.parse(sceneManageEntity.getFeatures());
                if (null != features) {
                    Long scriptId = features.getLong("scriptId");
                    stringRedisTemplate.opsForHash().put(String.format(SceneStartCheckConstants.SCENE_KEY, input.getSceneId()),
                        SceneStartCheckConstants.SCRIPT_ID_KEY, String.valueOf(scriptId));
                }
            }
        }
        //文件是否继续读取
        sceneData.setContinueRead(input.getContinueRead());

        //启动前置校验
        preCheckStart(sceneData, input);

        //创建临时报表数据
        ReportEntity report = initReport(sceneData, input);

        SceneActionOutput sceneAction = new SceneActionOutput();
        sceneAction.setData(report.getId());
        // 报告已经完成，则退出
        if (report.getStatus() == ReportConstants.FINISH_STATUS) {
            //失败状态
            JSONObject jb = JSON.parseObject(report.getFeatures());
            sceneAction.setMsg(Arrays.asList(jb.getString(ReportConstants.PRESSURE_MSG).split(",")));
            return sceneAction;
        }
        //230216 移动云-开放云不冻结流量
        //流量冻结
//        frozenAccountFlow(input, report, sceneData);

        // 清除SLA条件缓存
        stringRedisTemplate.opsForHash().delete(SlaServiceImpl.SLA_SCENE_KEY, String.valueOf(input.getSceneId()));
        //设置缓存，用以检查压测场景启动状态
        taskStatusCache.cacheStatus(input.getSceneId(), report.getId(), SceneRunTaskStatusEnum.STARTING);
        //缓存pod数量，上传jmeter日志时判断是否所有文件都上传完成
        taskStatusCache.cachePodNum(input.getSceneId(), sceneData.getIpNum());

        String engineInstanceRedisKey = PressureInstanceRedisKey.getEngineInstanceRedisKey(input.getSceneId(),
            report.getId(), report.getTenantId());
        List<String> activityRefs = sceneData.getBusinessActivityConfig().stream().map(
                SceneManageWrapperOutput.SceneBusinessActivityRefOutput::getBindRef)
            .collect(Collectors.toList());

        stringRedisTemplate.opsForHash().put(
            engineInstanceRedisKey,
            PressureInstanceRedisKey.SecondRedisKey.ACTIVITY_REFS,
            JsonHelper.bean2Json(activityRefs));
        //广播事件
        sceneTaskEventService.callStartEvent(sceneData, report.getId(), input.getPlaceholderMap(),
                input.getExclusiveEngine());

        return sceneAction;
    }

    @Override
    public void stop(Long sceneId) {
        SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
        if (sceneManage == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, "压测场景不存在" + sceneId);
        }
        //压测场景已经关闭，不做处理
        if (SceneManageStatusEnum.ifFree(sceneManage.getStatus())) {
            return;
        }
        ReportResult reportResult = reportDao.getReportBySceneId(sceneId);

        if (reportResult != null) {
            sceneTaskEventService.callStopEvent(reportResult);
        }
    }

    /**
     * 停止场景测试
     * <p>直接模式-手工补偿</p>
     * <ui>
     * <li>重置场景状态为0</li>
     * <li>重置对应的最新的压测报告状态为2</li>
     * </ui>
     *
     * @param sceneId 场景主键
     */
    @Override
    public int blotStop(Long sceneId) {
        SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
        if (sceneManage == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, "停止压测失败，场景不存在!");
        }

        ReportResult report = reportDao.getReportBySceneId(sceneId);
        if (report == null) {
            return 1;
        }

        // 数据库直接修改
        if (SceneManageStatusEnum.ifFree(sceneManage.getStatus())) {
            int sceneUpdateResult = sceneManageDAO.getBaseMapper().update(
                null,
                Wrappers.lambdaUpdate(SceneManageEntity.class)
                    .set(SceneManageEntity::getStatus, 0)
                    .eq(SceneManageEntity::getId, sceneId));

            if (sceneUpdateResult == 1) {
                ReportUpdateParam reportUpdateParam = new ReportUpdateParam();
                reportUpdateParam.setId(report.getId());
                reportUpdateParam.setStatus(2);
                reportDao.updateReport(reportUpdateParam);
            }
            return 1;
        }

        return 2;
    }

    @Override
    public SceneActionOutput checkSceneTaskStatus(Long sceneId, Long reportId) {
        //为如果传入报告id，以报告id为准
        SceneActionOutput scene = new SceneActionOutput();
        SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
        ReportResult reportResult = null;
        if (reportId != null) {
            reportResult = reportDao.selectById(reportId);
            //如果报告状态是已结束，查询结果为已结束
            if (reportResult.getStatus() != null && reportResult.getStatus() > 0) {
                scene.setData(0L);
            } else {
                // 需要判断下场景状态
                if (sceneManage != null) {
                    // 监测启动状态
                    scene.setData(SceneManageStatusEnum.getAdaptStatus(sceneManage.getStatus()).longValue());
                }else {
                    scene.setData(SceneManageStatusEnum.PRESSURE_TESTING.getValue().longValue());
                }
            }
        } else {
            if (sceneManage != null) {
                // 监测启动状态
                scene.setData(SceneManageStatusEnum.getAdaptStatus(sceneManage.getStatus()).longValue());
                if (sceneManage.getStatus() >= 0) {
                    reportResult = reportDao.getReportBySceneId(sceneId);
                }
            }
        }
        // 补充下 启动pod 个数
        String pressureNodeName = ScheduleConstants.getPressureNodeName(sceneId, reportId, sceneManage.getTenantId());
        String pressureNodeNum = stringRedisTemplate.opsForValue().get(pressureNodeName);
        String podInfo = String.format("目前已启动节点个数【%s】,请稍后", StringUtils.isBlank(pressureNodeNum) ? 0 : pressureNodeNum);
        scene.setMsg(Lists.newArrayList(podInfo));

        if (reportResult != null) {
            // 记录错误信息
            List<String> errorMessageList = Lists.newArrayList();
            // 检查压测引擎返回内容
            SceneRunTaskStatusOutput status = taskStatusCache.getStatus(sceneId, reportResult.getId());
            if (Objects.nonNull(status) && Objects.nonNull(status.getTaskStatus())
                && status.getTaskStatus() == SceneRunTaskStatusEnum.FAILED.getCode()) {
                errorMessageList.add(SceneStopReasonEnum.ENGINE.getType() + ":" + status.getErrorMsg());
            }
            scene.setReportId(reportResult.getId());
            if (StringUtils.isNotEmpty(reportResult.getFeatures())) {
                JSONObject jb = JSON.parseObject(reportResult.getFeatures());
                errorMessageList.add(jb.getString(ReportConstants.FEATURES_ERROR_MSG));
                errorMessageList.add(jb.getString(ReportConstants.PRESSURE_MSG));
            }
            if (CollectionUtils.isNotEmpty(errorMessageList)) {
                scene.setMsg(errorMessageList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()));
                //  前端只有等于0,才会显示错误
                scene.setData(0L);
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
        return sceneTaskEventService.callStartResultEvent(param);
    }

    @Override
    public void updateSceneTaskTps(SceneTaskUpdateTpsInput input) {
        // 补充租户信息
        CloudPluginUtils.fillUserData(input);
        // 设置动态值
        dynamicTpsService.set(input.getSceneId(), input.getReportId(), input.getTenantId(), input.getXpathMd5(), input.getTpsNum());
    }

    @Override
    public double queryAdjustTaskTps(SceneTaskQueryTpsInput input) {
        // 补充租户信息
        CloudPluginUtils.fillUserData(input);
        // 声明返回值字段
        double result;
        // 获取动态值
        Double dynamicValue = dynamicTpsService.get(input.getSceneId(), input.getReportId(), input.getTenantId(), input.getXpathMd5());
        // 如果动态值为空,则获取静态值
        if (dynamicValue != null) {result = dynamicValue;}
        // 获取静态值
        else {
            try {
                result = dynamicTpsService.getStatic(input.getReportId(), input.getXpathMd5());
            } catch (Exception e) {
                log.warn("获取静态TPS值失败.", e);
                result = 0.0;
            }
        }
        return result;
    }

    @Override
    public Long startFlowDebugTask(SceneManageWrapperInput input, List<EnginePluginInput> enginePlugins) {
        Long sceneManageId;
        CloudPluginUtils.fillUserData(input);
        //首先根据脚本实例id构建压测场景名称
        String pressureTestSceneName = SceneManageConstant.SCENE_MANAGER_FLOW_DEBUG + input.getTenantId() + "_" + input.getScriptDeployId();

        //根据场景名称查询是否已经存在场景
        SceneManageListResult sceneManageResult = sceneManageDAO.queryBySceneName(pressureTestSceneName);

        //不存在，新增压测场景
        if (sceneManageResult == null) {

            input.setPressureTestSceneName(pressureTestSceneName);
            input.setPressureType(PressureSceneEnum.FLOW_DEBUG.getCode());
            // 后续会根据传入并发数进行修改
            input.setConcurrenceNum(1);
            input.setIpNum(1);
            input.setPressureTestTime(new TimeBean(30L, "m"));
            input.setPressureMode(PressureModeEnum.FIXED.getCode());
            input.setType(PressureSceneEnum.FLOW_DEBUG.getCode());

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
        sceneTaskStartInput.setEnginePlugins(enginePlugins);
        sceneTaskStartInput.setContinueRead(false);
        SceneBusinessActivityRefInput activityRefInput = input.getBusinessActivityConfig().get(0);
        sceneTaskStartInput.setAssetType(AssetTypeEnum.ACTIVITY_CHECK.getCode());
        sceneTaskStartInput.setResourceId(activityRefInput.getBusinessActivityId());
        sceneTaskStartInput.setResourceName(activityRefInput.getBusinessActivityName());
        // 设置用户主键
        sceneTaskStartInput.setOperateId(CloudPluginUtils.getUserId());
        sceneTaskStartInput.setPlaceholderMap(input.getPlaceholderMap());
        SceneActionOutput sceneActionDTO = startTask(sceneTaskStartInput);
        //返回报告id
        return sceneActionDTO.getData();
    }

    @Override
    public SceneInspectTaskStartOutput startInspectTask(SceneManageWrapperInput input,
        List<EnginePluginInput> enginePlugins) {
        CloudPluginUtils.fillUserData(input);
        SceneInspectTaskStartOutput startOutput = new SceneInspectTaskStartOutput();
        Long sceneManageId = null;
        //首先根据脚本实例id构建压测场景名称
        String pressureTestSceneName = SceneManageConstant.SCENE_MANAGER_INSPECT + input.getTenantId() + "_" + input.getScriptId();

        //根据场景名称查询是否已经存在场景
        SceneManageListResult sceneManageResult = sceneManageDAO.queryBySceneName(pressureTestSceneName);

        //不存在，新增压测场景
        if (sceneManageResult == null) {
            input.setPressureTestSceneName(pressureTestSceneName);
            input.setPressureType(PressureSceneEnum.INSPECTION_MODE.getCode());
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
                String desc = null != statusEnum ? statusEnum.getDesc() : "";
                String errMsg = "启动巡检场景失败，场景前置状态校验失败:" + desc;
                log.error("异常代码【{}】,异常内容：启动巡检场景失败 --> 场景前置状态校验失败: {}",
                    TakinCloudExceptionEnum.INSPECT_TASK_START_ERROR, desc);
                startOutput.setSceneId(sceneManageId);
                startOutput.setMsg(Collections.singletonList(errMsg));
                return startOutput;
            }
            sceneManageId = sceneManageResult.getId();
        }

        //启动该压测场景
        SceneTaskStartInput sceneTaskStartInput = new SceneTaskStartInput();
        sceneTaskStartInput.setSceneId(sceneManageId);
        sceneTaskStartInput.setEnginePlugins(enginePlugins);
        Long fixTimer = input.getFixTimer();
        Integer loopsNum = input.getLoopsNum();
        SceneInspectInput inspectInput = new SceneInspectInput().setFixedTimer(fixTimer).setLoopsNum(loopsNum);
        sceneTaskStartInput.setSceneInspectInput(inspectInput);
        sceneTaskStartInput.setContinueRead(false);
        sceneTaskStartInput.setPlaceholderMap(input.getPlaceholderMap());
        SceneActionOutput sceneActionOutput = startTask(sceneTaskStartInput);
        startOutput.setSceneId(sceneManageId);
        startOutput.setReportId(sceneActionOutput.getData());
        //开始试跑就设置一个状态，后面区分试跑任务和正常压测
        String key = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneManageId,
            sceneActionOutput.getData());
        stringRedisTemplate.opsForHash().put(key, SceneTaskRedisConstants.SCENE_RUN_TASK_STATUS_KEY,
            SceneRunTaskStatusEnum.STARTING.getText());
        return startOutput;
    }

    @Override
    public SceneTaskStopOutput forceStopTask(Long reportId, boolean isNeedFinishReport) {
        SceneTaskStopOutput r = new SceneTaskStopOutput();
        r.setReportId(reportId);
        try {
            ReportResult report = reportDao.selectById(reportId);
            if (null == report) {
                r.addMsg("任务不存在");
                return r;
            }

            String jobName = ScheduleConstants.getScheduleName(report.getSceneId(), reportId, report.getTenantId());
            String engineInstanceRedisKey = PressureInstanceRedisKey.getEngineInstanceRedisKey(report.getSceneId(), reportId, report.getTenantId());
            engineService.deleteJob(jobName, engineInstanceRedisKey);

            // 触发强制停止
            if (isNeedFinishReport && ReportConstants.INIT_STATUS == (report.getStatus()) && null != report.getStartTime()) {
                ReportUpdateParam param = new ReportUpdateParam();
                param.setId(reportId);
                param.setStatus(ReportConstants.RUN_STATUS);
                if (null == report.getEndTime()) {
                    param.setEndTime(Calendar.getInstance().getTime());
                }
                reportDao.updateReport(param);
            } else if (ReportConstants.FINISH_STATUS != (report.getStatus())) {
                reportService.forceFinishReport(reportId);
            }
        } catch (Throwable t) {
            r.addMsg("程序抛异常了");
            log.error("forceStopTask failed！", t);
        }
        return r;
    }

    @Override
    public SceneInspectTaskStopOutput stopInspectTask(Long sceneId) {
        SceneInspectTaskStopOutput output = new SceneInspectTaskStopOutput();
        output.setSceneId(sceneId);
        SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
        if (!Objects.isNull(sceneManage)) {
            SceneManageStatusEnum statusEnum = SceneManageStatusEnum.getSceneManageStatusEnum(sceneManage.getStatus());
            if (!SceneManageStatusEnum.getWorking().contains(statusEnum)
                && !SceneManageStatusEnum.getFree().contains(statusEnum)) {
                String errMsg = "停止巡检场景失败，场景前置状态校验失败:" + (null != statusEnum ? statusEnum.getDesc() : "");
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
    public SceneTryRunTaskStartOutput startTryRun(SceneManageWrapperInput input,
        List<EnginePluginInput> enginePlugins) {
        Long sceneManageId;
        CloudPluginUtils.fillUserData(input);
        //首先根据脚本实例id构建压测场景名称
        String pressureTestSceneName = SceneManageConstant.SCENE_MANAGER_TRY_RUN + input.getTenantId() + "_" + input
            .getScriptDeployId();
        //根据场景名称查询是否已经存在场景
        SceneManageListResult sceneManageResult = sceneManageDAO.queryBySceneName(pressureTestSceneName);
        SceneTryRunTaskStartOutput sceneTryRunTaskStartOutput = new SceneTryRunTaskStartOutput();
        CloudPluginUtils.fillUserData(sceneTryRunTaskStartOutput);
        //不存在，新增压测场景
        if (sceneManageResult == null) {

            input.setPressureTestSceneName(pressureTestSceneName);
            input.setPressureType(PressureSceneEnum.TRY_RUN.getCode());
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
        sceneTaskStartInput.setEnginePlugins(enginePlugins);
        sceneTaskStartInput.setContinueRead(false);
        //TODO 根据次数，设置时间
        SceneTryRunInput tryRunInput = new SceneTryRunInput(input.getLoopsNum(), input.getConcurrencyNum());
        sceneTaskStartInput.setSceneTryRunInput(tryRunInput);
        sceneTaskStartInput.setAssetType(AssetTypeEnum.SCRIPT_DEBUG.getCode());
        sceneTaskStartInput.setResourceId(input.getScriptDeployId());
        sceneTaskStartInput.setResourceName(input.getScriptName());
        sceneTaskStartInput.setOperateId(input.getOperateId());
        sceneTaskStartInput.setOperateName(input.getOperateName());
        sceneTaskStartInput.setPlaceholderMap(input.getPlaceholderMap());
        sceneTaskStartInput.setExclusiveEngine(input.getExclusiveEngine());
        SceneActionOutput sceneActionOutput = startTask(sceneTaskStartInput);
        sceneTryRunTaskStartOutput.setReportId(sceneActionOutput.getData());

        return sceneTryRunTaskStartOutput;
    }

    @Override
    public SceneTryRunTaskStatusOutput checkTaskStatus(Long sceneId, Long reportId) {
        String key = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneId, reportId);
        Object status = stringRedisTemplate.opsForHash().get(key, SceneTaskRedisConstants.SCENE_RUN_TASK_STATUS_KEY);
        SceneTryRunTaskStatusOutput output = new SceneTryRunTaskStatusOutput();
        if (Objects.nonNull(status)) {
            SceneRunTaskStatusEnum statusEnum = SceneRunTaskStatusEnum.getTryRunTaskStatusEnumByText(status.toString());
            if (Objects.isNull(statusEnum)) {
                output.setTaskStatus(SceneRunTaskStatusEnum.STARTING.getCode());
                return output;
            }
            output.setTaskStatus(statusEnum.getCode());
            if (statusEnum.equals(SceneRunTaskStatusEnum.FAILED)) {
                Object errorObj = stringRedisTemplate.opsForHash().get(key, SceneTaskRedisConstants.SCENE_RUN_TASK_ERROR);
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
        SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
        if (Objects.isNull(sceneManage)) {
            state.setState(SceneManageConstant.SCENE_TASK_JOB_STATUS_NONE);
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
            state.setState(SceneManageConstant.SCENE_TASK_JOB_STATUS_NONE);
            state.setMsg("压测任务未启动");
            return state;
        }
        if (StringUtils.isEmpty(reportId)) {
            state.setState(SceneManageConstant.SCENE_TASK_JOB_STATUS_NONE);
            state.setMsg("未获取到相应压测报告");
            return state;
        }
        String jobName = ScheduleConstants.getScheduleName(sceneId, Long.parseLong(reportId), sceneManage.getTenantId());
        EngineCallExtApi engineCallExtApi = enginePluginUtils.getEngineCallExtApi();
        String status = engineCallExtApi.getJobStatus(jobName);
        state.setState(status);

        if (Objects.equals(status, SceneManageConstant.SCENE_TASK_JOB_STATUS_RUNNING)) {
            state.setMsg("任务执行中");
        } else if (Objects.equals(status, SceneManageConstant.SCENE_TASK_JOB_STATUS_NONE)) {
            state.setMsg("任务已停止");
        } else {
            state.setMsg("任务执行错误");
        }
        return state;
    }

    /**
     * 场景启动前置校验
     */
    private void preCheckStart(SceneManageWrapperOutput sceneData, SceneTaskStartInput input) {
        // 流量判断
        if (null == sceneData.getTenantId()) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "场景没有绑定客户信息");
        }
        // 开放云不限定流量 230216
//        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
//        if (assetExtApi != null) {
//            AccountInfoExt account = assetExtApi.queryAccount(sceneData.getTenantId(), input.getOperateId());
//            if (null == account || account.getBalance().compareTo(sceneData.getEstimateFlow()) < 0) {
//                throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "压测流量不足！");
//            }
//        }

        if (!SceneManageStatusEnum.ifFree(sceneData.getStatus())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "当前场景不为待启动状态！");
        }
        //检测脚本文件是否有变更
        SceneScriptRefOutput scriptRefOutput = sceneData.getUploadFile().stream().filter(Objects::nonNull)
            .filter(fileRef -> fileRef.getFileType() == 0 && fileRef.getFileName().endsWith(SCRIPT_NAME_SUFFIX))
            .findFirst()
            .orElse(null);

        boolean jmxCheckResult = checkOutJmx(scriptRefOutput, sceneData.getId());
        if (!jmxCheckResult) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_JMX_FILE_CHECK_ERROR,
                "启动压测场景--场景ID:" + sceneData.getId() + ",脚本文件校验失败！");
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
        // 校验是否与场景同步了
        {
            String disabledKey = "DISABLED";
            String featureString = sceneData.getFeatures();
            Map<String, Object> feature = JSONObject.parseObject(featureString, new TypeReference<Map<String, Object>>() {});
            if (feature.containsKey(disabledKey)) {
                throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR,
                    "场景【" + sceneData.getId() + "】对应的业务流程发生变更，未能自动匹配，请手动编辑后启动压测");
            }
        }
    }

    /**
     * 初始化报表
     *
     * @return -
     */
    public ReportEntity initReport(SceneManageWrapperOutput scene, SceneTaskStartInput input) {
        ReportEntity report = new ReportEntity();
        report.setSceneId(scene.getId());
        report.setConcurrent(scene.getConcurrenceNum());
        report.setStatus(ReportConstants.INIT_STATUS);
        // 初始化
        report.setEnvCode(scene.getEnvCode());
        report.setTenantId(scene.getTenantId());
        report.setEnvCode(scene.getEnvCode());
        report.setOperateId(input.getOperateId());
        // 解决开始时间 偏移10s
        report.setStartTime(new Date(System.currentTimeMillis() + offsetStartTime * 1000));
        //负责人默认启动人
        report.setUserId(CloudPluginUtils.getUserId());
        report.setSceneName(scene.getPressureTestSceneName());

        if (StringUtils.isNotBlank(scene.getFeatures())) {
            JSONObject features = JsonUtil.parse(scene.getFeatures());
            if (null != features && features.containsKey(SceneManageConstant.FEATURES_SCRIPT_ID)) {
                report.setScriptId(features.getLong(SceneManageConstant.FEATURES_SCRIPT_ID));
            }
        }
        Integer sumTps = CommonUtil.sum(scene.getBusinessActivityConfig(), SceneManageWrapperOutput.SceneBusinessActivityRefOutput::getTargetTPS);

        report.setTps(sumTps);
        report.setPressureType(scene.getPressureType());
        report.setType(scene.getType());
        if (StringUtils.isNotBlank(scene.getScriptAnalysisResult())) {
            report.setScriptNodeTree(JsonPathUtil.deleteNodes(scene.getScriptAnalysisResult()).jsonString());
        }
        reportMapper.insert(report);

        //标记场景
        // 待启动,压测失败，停止压测（压测工作已停止） 强制停止 ---> 启动中
        Boolean updateFlag = sceneManageService.updateSceneLifeCycle(
            UpdateStatusBean.build(scene.getId(), report.getId(), scene.getTenantId())
                .checkEnum(SceneManageStatusEnum.WAIT, SceneManageStatusEnum.FAILED, SceneManageStatusEnum.STOP, SceneManageStatusEnum.FORCE_STOP)
                .updateEnum(SceneManageStatusEnum.STARTING).build());
        if (!updateFlag) {
            //失败状态 获取最新的报告
            return reportMapper.selectById(report.getId());
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
            if (null != activity.getTargetTPS()) {
                reportBusinessActivityDetail.setTargetTps(new BigDecimal(activity.getTargetTPS()));
            }
            if (null != activity.getTargetRT()) {
                reportBusinessActivityDetail.setTargetRt(new BigDecimal(activity.getTargetRT()));
            }
            reportBusinessActivityDetail.setTargetSuccessRate(activity.getTargetSuccessRate());
            reportBusinessActivityDetail.setTargetSa(activity.getTargetSA());
            reportBusinessActivityDetailDao.insert(reportBusinessActivityDetail);
        });
        saveNonTargetNode(scene.getId(), reportId, report.getScriptNodeTree(), scene.getBusinessActivityConfig());
        log.info("启动[{}]场景测试，初始化报表数据,报表ID: {}", scene.getId(), report.getId());
        return report;
    }

    /**
     * 把节点树中的测试计划、线程组、控制器当作业务活动插入到报告关联的业务活动中
     *
     * @param sceneId                场景ID
     * @param reportId               报告ID
     * @param scriptNodeTree         节点树
     * @param businessActivityConfig 场景业务活动信息
     */
    private void saveNonTargetNode(Long sceneId, Long reportId, String scriptNodeTree,
        List<SceneBusinessActivityRefOutput> businessActivityConfig) {
        if (StringUtils.isBlank(scriptNodeTree) || CollectionUtils.isEmpty(businessActivityConfig)) {
            return;
        }
        List<String> bindRefList = businessActivityConfig.stream().filter(Objects::nonNull)
            .map(SceneBusinessActivityRefOutput::getBindRef)
            .collect(Collectors.toList());
        List<ReportBusinessActivityDetail> resultList = new ArrayList<>();
        List<ScriptNode> testPlanNodeList = JsonPathUtil.getCurrentNodeByType(scriptNodeTree,
            NodeTypeEnum.TEST_PLAN.name());
        if (CollectionUtils.isNotEmpty(testPlanNodeList) && testPlanNodeList.size() == 1) {
            ScriptNode scriptNode = testPlanNodeList.get(0);
            fillNonTargetActivityDetail(sceneId, reportId, scriptNode, resultList);
        }
        List<ScriptNode> threadGroupNodes = JsonPathUtil.getCurrentNodeByType(scriptNodeTree,
            NodeTypeEnum.THREAD_GROUP.name());
        if (CollectionUtils.isNotEmpty(threadGroupNodes)) {
            threadGroupNodes.stream().filter(Objects::nonNull)
                .forEach(node -> fillNonTargetActivityDetail(sceneId, reportId, node, resultList));
        }
        List<ScriptNode> controllerNodes = JsonPathUtil.getCurrentNodeByType(scriptNodeTree,
            NodeTypeEnum.CONTROLLER.name());
        if (CollectionUtils.isNotEmpty(controllerNodes)) {
            controllerNodes.stream().filter(Objects::nonNull)
                .filter(node -> !bindRefList.contains(node.getXpathMd5()))
                .forEach(node -> fillNonTargetActivityDetail(sceneId, reportId, node, resultList));
        }
        if (CollectionUtils.isNotEmpty(resultList)) {
            resultList.stream().filter(Objects::nonNull)
                .forEach(detail -> reportBusinessActivityDetailDao.insert(detail));
        }

    }

    /**
     * 计算子节点的目标值
     *
     * @param sceneId    场景ID
     * @param reportId   报告ID
     * @param scriptNode 目标节点
     * @param detailList 结果
     */
    private void fillNonTargetActivityDetail(Long sceneId, Long reportId, ScriptNode scriptNode, List<ReportBusinessActivityDetail> detailList) {
        ReportBusinessActivityDetail detail = new ReportBusinessActivityDetail();
        detail.setTargetTps(new BigDecimal(-1));
        detail.setTargetRt(new BigDecimal(-1));
        detail.setTargetSa(new BigDecimal(-1));
        detail.setTargetSuccessRate(new BigDecimal(-1));
        detail.setSceneId(sceneId);
        detail.setReportId(reportId);
        detail.setBusinessActivityId(-1L);
        detail.setBusinessActivityName(scriptNode.getTestName());
        detail.setBindRef(scriptNode.getXpathMd5());
        detailList.add(detail);
    }

    /**
     * 压测任务正常开启 这里实际是 压力节点 启动成功
     * 20200923 报告 开始时间 记录在redis 中
     *
     * @see CollectorService
     **/
    @Transactional(rollbackFor = Exception.class)
    public synchronized void testStarted(TaskResult taskResult) {
        log.info("场景[{}-{}-{}]启动新的压测任务", taskResult.getSceneId(), taskResult.getTaskId(), taskResult.getTenantId());
        //场景压测进行中
        // job创建中 改成 pod工作中 隐式 状态严格 更新 解决 多pod 问题
        // 进行计数
        String pressureNodeName = ScheduleConstants.getPressureNodeName(taskResult.getSceneId(), taskResult.getTaskId(),
            taskResult.getTenantId());

        // cloud集群 redis同步操作，increment 直接拿数据，无需重新获取key的value
        Long num = stringRedisTemplate.opsForValue().increment(pressureNodeName, 1);
        log.info("当前启动pod成功数量=【{}】", num);
        if (Long.valueOf(1).equals(num)) {
            // 启动只更新一次
            sceneManageService.updateSceneLifeCycle(
                UpdateStatusBean.build(taskResult.getSceneId(), taskResult.getTaskId(), taskResult.getTenantId())
                    .checkEnum(SceneManageStatusEnum.JOB_CREATING)
                    .updateEnum(SceneManageStatusEnum.PRESSURE_NODE_RUNNING).build());
        }

    }

    /**
     * 压测任务启动失败
     */
    @Transactional(rollbackFor = Exception.class)
    public void testFailed(TaskResult taskResult) {
        log.info("场景[{}]压测任务启动失败，失败原因:{}", taskResult.getSceneId(), taskResult.getMsg());
        ReportEntity report = reportMapper.selectById(taskResult.getTaskId());
        if (report != null && report.getStatus() == ReportConstants.INIT_STATUS) {
            //删除报表
            report.setGmtUpdate(new Date());
            report.setIsDeleted(1);
            report.setId(taskResult.getTaskId());
            String amountLockId = null;
            JSONObject json = JsonUtil.parse(report.getFeatures());
            if (null == json) {
                json = new JSONObject();
            } else {
                amountLockId = json.getString("lockId");
            }
            json.put(ReportConstants.FEATURES_ERROR_MSG, taskResult.getMsg());
            report.setFeatures(json.toJSONString());
            reportMapper.updateById(report);

            //释放流量
            AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
            if (assetExtApi != null) {
                boolean unLock;
                if (StringUtils.isNotBlank(amountLockId)) {
                    unLock = assetExtApi.unlock(taskResult.getTenantId(), amountLockId);
                } else {
                    unLock = assetExtApi.unlock(report.getTenantId(), taskResult.getTaskId().toString());
                }
                if (!unLock) {
                    log.error("释放流量失败！");
                }
            }
            ReportResult recentlyReport = reportDao.getRecentlyReport(taskResult.getSceneId());
            if (!taskResult.getTaskId().equals(recentlyReport.getId())) {
                log.error("更新压测生命周期，所更新的报告不是压测场景的最新报告,场景id:{},更新的报告id:{},当前最新的报告id:{}",
                    taskResult.getSceneId(), taskResult.getTaskId(), recentlyReport.getId());
                return;
            }
            sceneManageDao.getBaseMapper().updateById(new SceneManageEntity() {{
                setId(taskResult.getSceneId());
                setUpdateTime(new Date());
                setStatus(SceneManageStatusEnum.WAIT.getValue());
            }});
        }
    }

    @Override
    public SceneTaskStartCheckOutput sceneStartCsvPositionCheck(SceneTaskStartCheckInput input) {
        //1.查询缓存是否有值
        //2.查询pod数量和文件名,进行比较
        //3.查询缓存值是否小于预分片end
        //4.查询脚本是否变更
        SceneTaskStartCheckOutput output = new SceneTaskStartCheckOutput();
        try {
            SceneManageWrapperOutput sceneManage = sceneManageService.getSceneManage(input.getSceneId(),
                new SceneManageQueryOpitons() {{
                    setIncludeBusinessActivity(false);
                    setIncludeScript(true);
                    setIncludeSLA(false);
                }});
            input.setPodNum(sceneManage.getIpNum());
            long sceneId = input.getSceneId();
            List<SceneScriptRefOutput> uploadFile = sceneManage.getUploadFile();
            if (CollectionUtils.isEmpty(uploadFile)) {
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
            if (!scriptChange) {
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
                if (MapUtils.isNotEmpty(positionMap)) {
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
        } catch (Exception e) {
            log.error("获取文件读取位点信息失败：场景ID：{}，错误信息:{}", input.getSceneId(), e.getMessage());
            output.setHasUnread(false);
        }
        return output;
    }

    @Override
    public void writeBalance(AssetBalanceExt balanceExt) {
        log.warn("回写流量接收到入参:{}", JSON.toJSONString(balanceExt));
        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (assetExtApi != null) {
            assetExtApi.writeBalance(balanceExt);
        }
    }

    private Boolean compareScript(long sceneId, String scriptId) {
        Object scriptIdObj = stringRedisTemplate.opsForHash().get(String.format(SceneStartCheckConstants.SCENE_KEY, sceneId), SceneStartCheckConstants.SCRIPT_ID_KEY);
        return scriptIdObj != null && scriptId.equals(scriptIdObj.toString());
    }

    private boolean comparePod(long sceneId, int podNum) {
        Object podObj = stringRedisTemplate.opsForHash().get(ScheduleConstants.SCHEDULE_POD_NUM, String.valueOf(sceneId));
        if (Objects.nonNull(podObj)) {
            int cachedPodNum = Integer.parseInt(podObj.toString());
            return cachedPodNum == podNum;
        }
        return false;
    }

    private void comparePosition(SceneTaskStartCheckOutput output, long sceneId, String fileName, int podNum, boolean isSplit,
        Map<Object, Object> positionMap) {
        SceneBigFileSliceEntity sliceEntity = fileSliceService.getOneByParam(new FileSliceRequest() {{
            setSceneId(sceneId);
            setFileName(fileName);
        }});
        if (Objects.isNull(sliceEntity) || sliceEntity.getSliceCount() != podNum || StringUtils.isBlank(
            sliceEntity.getSliceInfo())) {
            output.setHasUnread(false);
            return;
        }

        List<FileReadInfo> fileReadInfos = output.getFileReadInfos();
        if (CollectionUtils.isEmpty(fileReadInfos)) {
            fileReadInfos = new ArrayList<>();
        }
        List<StartEndPair> startEndPairs = JSONArray.parseArray(sliceEntity.getSliceInfo(), StartEndPair.class);
        long fileSize = startEndPairs.stream().filter(Objects::nonNull)
            .mapToLong(StartEndPair::getEnd)
            .filter(Objects::nonNull)
            .max()
            .orElse(0L);
        if (fileSize == 0) {
            output.setHasUnread(false);
            return;
        }
        //文件拆分，计算已读、文件大小
        if (isSplit) {
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
            FileReadInfo info = new FileReadInfo() {{
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
            for (Entry<Object, Object> entry : positionMap.entrySet()) {
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
        stringRedisTemplate.delete(key);
    }

    private String getPositionSize(Long position) {
        if (position > TB) {
            return position / TB + "TB";
        }
        if (position > GB) {
            return position / GB + "GB";
        }
        if (position > MB) {
            return position / MB + "MB";
        }
        if (position > KB) {
            return position / KB + "KB";
        }
        return position + "B";
    }

    private boolean checkOutJmx(SceneScriptRefOutput uploadFile, Long sceneId) {
        if (Objects.nonNull(uploadFile) && StringUtils.isNotBlank(uploadFile.getUploadPath())) {
            String uploadPath = uploadFile.getUploadPath();
            String[] split = uploadPath.split("/");
            //这里做个相对路径兼容
            String destPath = sceneManageService.getDestPath(sceneId);
            if (destPath.endsWith(split[0] + SceneManageConstant.FILE_SPLIT)){
                uploadPath = destPath + split[1];
            }
            String fileMd5 = MD5Utils.getInstance().getMD5(new File(uploadPath));
            if (StringUtils.isNotBlank(uploadFile.getFileMd5())) {
                return uploadFile.getFileMd5().equals(fileMd5);
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 冻结流量
     *
     * @param input     {@link SceneTaskStartInput}
     * @param report    {@link Report}
     * @param sceneData {@link SceneManageWrapperOutput}
     */
    private void frozenAccountFlow(SceneTaskStartInput input, ReportEntity report, SceneManageWrapperOutput sceneData) {
        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (assetExtApi != null) {
            //得到数据来源ID
            Long resourceId = input.getResourceId();
            if (AssetTypeEnum.PRESS_REPORT.getCode().equals(input.getAssetType())) {
                resourceId = report.getId();
            }
            AssetInvoiceExt<List<AssetBillExt>> invoice = new AssetInvoiceExt<>();
            invoice.setSceneId(sceneData.getId());
            invoice.setTaskId(report.getId());
            invoice.setResourceId(resourceId);
            invoice.setResourceType(input.getAssetType());
            invoice.setResourceName(input.getResourceName());
            invoice.setOperateId(input.getOperateId());
            invoice.setOperateName(input.getOperateName());
            invoice.setCustomerId(report.getTenantId());
            AssetBillExt.TimeBean pressureTestTime = new AssetBillExt.TimeBean(sceneData.getTotalTestTime(),
                TimeUnitEnum.SECOND.getValue());
            String testTimeCost = DataUtils.formatTime(sceneData.getTotalTestTime());
            if (MapUtils.isNotEmpty(sceneData.getThreadGroupConfigMap())) {
                List<AssetBillExt> bills = sceneData.getThreadGroupConfigMap().values().stream()
                    .filter(Objects::nonNull)
                    .map(config -> {
                        AssetBillExt bill = new AssetBillExt();
                        bill.setIpNum(sceneData.getIpNum());
                        bill.setConcurrenceNum(config.getThreadNum());
                        bill.setPressureTestTime(pressureTestTime);
                        bill.setPressureMode(config.getMode());
                        bill.setPressureScene(sceneData.getPressureType());
                        bill.setPressureType(config.getType());
                        if (null != config.getRampUp()) {
                            AssetBillExt.TimeBean rampUp = new AssetBillExt.TimeBean(config.getRampUp().longValue(),
                                config.getRampUpUnit());
                            bill.setIncreasingTime(rampUp);
                        }
                        bill.setStep(config.getSteps());
                        bill.setPressureTestTimeCost(testTimeCost);
                        return bill;
                    })
                    .collect(Collectors.toList());
                invoice.setData(bills);
            } else if (null != sceneData.getConcurrenceNum()) {
                AssetBillExt bill = new AssetBillExt();
                bill.setIpNum(sceneData.getIpNum());
                bill.setConcurrenceNum(sceneData.getConcurrenceNum());
                bill.setPressureTestTime(pressureTestTime);
                bill.setPressureMode(PressureModeEnum.FIXED.getCode());
                bill.setPressureScene(sceneData.getPressureType());
                bill.setPressureType(ThreadGroupTypeEnum.CONCURRENCY.getCode());
                bill.setPressureTestTimeCost(DataUtils.formatTime(sceneData.getTotalTestTime()));
                invoice.setData(Lists.newArrayList(bill));
            }
            try {
                Response<String> res = assetExtApi.lock(invoice);
                if (null != res && res.isSuccess() && StringUtils.isNotBlank(res.getData())) {
                    ReportUpdateParam rp = new ReportUpdateParam();
                    rp.setId(report.getId());
                    JSONObject features = JsonUtil.parse(report.getFeatures());
                    if (null == features) {
                        features = new JSONObject();
                    }
                    features.put("lockId", res.getData());
                    reportDao.updateReport(rp);
                } else {
                    log.error("流量冻结失败");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw e;
            }
        }
    }
}
