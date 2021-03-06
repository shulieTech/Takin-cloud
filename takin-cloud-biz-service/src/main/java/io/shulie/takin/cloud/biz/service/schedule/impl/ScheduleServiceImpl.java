package io.shulie.takin.cloud.biz.service.schedule.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.bean.BeanUtil;
import com.pamirs.takin.entity.dao.schedule.TScheduleRecordMapper;
import com.pamirs.takin.entity.domain.entity.schedule.ScheduleRecord;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageStartRecordVO;
import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.biz.output.engine.EngineLogPtlConfigOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.service.async.AsyncService;
import io.shulie.takin.cloud.biz.service.engine.EngineConfigService;
import io.shulie.takin.cloud.biz.service.record.ScheduleRecordEnginePluginService;
import io.shulie.takin.cloud.biz.service.report.ReportService;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.biz.service.schedule.ScheduleEventService;
import io.shulie.takin.cloud.biz.service.schedule.ScheduleService;
import io.shulie.takin.cloud.biz.service.strategy.StrategyConfigService;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOpitons;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.constants.PressureInstanceRedisKey;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.common.utils.EnginePluginUtils;
import io.shulie.takin.cloud.common.utils.NumberUtil;
import io.shulie.takin.cloud.ext.api.EngineCallExtApi;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleInitParamExt;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleRunRequest;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStartRequestExt;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStopRequestExt;
import io.shulie.takin.cloud.ext.content.enginecall.StrategyConfigExt;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.annotation.IntrestFor;
import io.shulie.takin.cloud.ext.content.enginecall.PtlLogConfigExt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ??????
 * @date 2020-05-12
 */
@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {
    @Resource
    private StrategyConfigService strategyConfigService;
    @Resource
    private TScheduleRecordMapper tScheduleRecordMapper;
    @Resource
    private ScheduleEventService scheduleEvent;
    @Resource
    private SceneManageService sceneManageService;
    @Resource
    private ScheduleRecordEnginePluginService scheduleRecordEnginePluginService;
    @Resource
    private AsyncService asyncService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    @Qualifier("stopThreadPool")
    protected ThreadPoolExecutor stopExecutor;
    @Resource
    private ReportService reportService;
    @Resource
    private EnginePluginUtils pluginUtils;
    @Resource
    private AppConfig appConfig;
    @Resource
    private EngineConfigService engineConfigService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void startSchedule(ScheduleStartRequestExt request) {
        log.info("????????????, ???????????????{}", request);
        //?????????????????????
        ScheduleRecord schedule = tScheduleRecordMapper.getScheduleByTaskId(request.getTaskId());
        if (schedule != null) {
            log.error("???????????????{}???,????????????????????????????????? --> ????????????[{}]????????????",
                TakinCloudExceptionEnum.SCHEDULE_START_ERROR, request.getTaskId());
            return;
        }
        //????????????
        StrategyConfigExt config = strategyConfigService.getCurrentStrategyConfig();
        if (config == null) {
            log.error("???????????????{}???,????????????????????????????????? --> ?????????????????????",
                TakinCloudExceptionEnum.SCHEDULE_START_ERROR);
            return;
        }

        String scheduleName = ScheduleConstants.getScheduleName(request.getSceneId(), request.getTaskId(), request.getTenantId());

        //??????????????????
        ScheduleRecord scheduleRecord = new ScheduleRecord();
        scheduleRecord.setCpuCoreNum(config.getCpuNum());
        scheduleRecord.setPodNum(request.getTotalIp());
        scheduleRecord.setMemorySize(config.getMemorySize());
        scheduleRecord.setSceneId(request.getSceneId());
        scheduleRecord.setTaskId(request.getTaskId());
        scheduleRecord.setStatus(ScheduleConstants.SCHEDULE_STATUS_1);

        scheduleRecord.setTenantId(request.getTenantId());
        scheduleRecord.setPodClass(scheduleName);
        tScheduleRecordMapper.insertSelective(scheduleRecord);

        //add by ?????? ????????????????????????????????????????????????
        scheduleRecordEnginePluginService.saveScheduleRecordEnginePlugins(
            scheduleRecord.getId(), request.getEnginePluginsFilePath());
        //add end

        //????????????
        ScheduleRunRequest eventRequest = new ScheduleRunRequest();
        eventRequest.setScheduleId(scheduleRecord.getId());
        eventRequest.setRequest(request);
        eventRequest.setStrategyConfig(config);
        String memSetting;
        if (PressureSceneEnum.INSPECTION_MODE.getCode().equals(request.getPressureScene())) {
            memSetting = "-XX:MaxRAMPercentage=90.0";
        } else {
            memSetting = CommonUtil.getValue(appConfig.getK8sJvmSettings(), config, StrategyConfigExt::getK8sJvmSettings);
        }
        eventRequest.setMemSetting(memSetting);
        eventRequest.setZkServers(appConfig.getZkServers());
        eventRequest.setLogQueueSize(NumberUtil.parseInt(appConfig.getLogQueueSize(), 25000));

        eventRequest.setPressureEngineBackendQueueCapacity(appConfig.getPressureEngineBackendQueueCapacity());
        eventRequest.setEngineRedisAddress(appConfig.getEngineRedisAddress());
        eventRequest.setEngineRedisPort(appConfig.getEngineRedisPort());
        eventRequest.setEngineRedisSentinelNodes(appConfig.getEngineRedisSentinelNodes());
        eventRequest.setEngineRedisSentinelMaster(appConfig.getEngineRedisSentinelMaster());
        eventRequest.setEngineRedisPassword(appConfig.getEngineRedisPassword());
        Integer traceSampling = 1;
        if (!request.isTryRun() && !request.isInspect()) {
            traceSampling = CommonUtil.getValue(traceSampling, engineConfigService, EngineConfigService::getLogSimpling);
        }
        eventRequest.setTraceSampling(traceSampling);
        EngineLogPtlConfigOutput engineLogPtlConfigOutput = engineConfigService.getEnginePtlConfig();
        PtlLogConfigExt ptlLogConfig = BeanUtil.copyProperties(engineLogPtlConfigOutput, PtlLogConfigExt.class);
        //??????ptl????????????????????????
        ptlLogConfig.setPtlUploadFrom(appConfig.getEngineLogUploadModel());
        eventRequest.setPtlLogConfig(ptlLogConfig);

        //???????????????????????????????????????????????????
        stringRedisTemplate.opsForValue().set(scheduleName, JSON.toJSONString(eventRequest));
        // ????????? ???????????? pod????????????redis,???????????????
        // ?????? ?????????????????? ?????????????????????????????????????????????24??????
        stringRedisTemplate.opsForValue().set(
            ScheduleConstants.getPressureNodeTotalKey(request.getSceneId(), request.getTaskId(), request.getTenantId()),
            String.valueOf(request.getTotalIp()), 1, TimeUnit.DAYS);
        //???????????????
        scheduleEvent.initSchedule(eventRequest);
    }

    @Override
    public void stopSchedule(ScheduleStopRequestExt request) {
        log.info("????????????, ???????????????{}", request);
        ScheduleRecord scheduleRecord = tScheduleRecordMapper.getScheduleByTaskId(request.getTaskId());
        if (scheduleRecord != null) {
            // ????????????
            String scheduleName = ScheduleConstants.getScheduleName(request.getSceneId(), request.getTaskId(), request.getTenantId());
            stringRedisTemplate.opsForValue().set(
                ScheduleConstants.INTERRUPT_POD + scheduleName,
                Boolean.TRUE.toString(), 1, TimeUnit.DAYS);
            if (!Boolean.parseBoolean(stringRedisTemplate.opsForValue().get(ScheduleConstants.FORCE_STOP_POD + scheduleName))) {
                // 3???????????????????????? ??????????????????
                stopExecutor.execute(new SceneStopThread(request));
            }
        }

    }

    @Override
    public void runSchedule(ScheduleRunRequest request) {
        ScheduleStartRequestExt startRequest = request.getRequest();
        // ?????????????????????
        push(startRequest);

        Long sceneId = startRequest.getSceneId();
        Long taskId = startRequest.getTaskId();
        Long customerId = startRequest.getTenantId();

        // ???????????????????????? ?????????(??????????????????) ---> ??????Job???
        sceneManageService.updateSceneLifeCycle(
            UpdateStatusBean.build(sceneId, taskId, customerId)
                .checkEnum(SceneManageStatusEnum.STARTING, SceneManageStatusEnum.FILE_SPLIT_END)
                .updateEnum(SceneManageStatusEnum.JOB_CREATING)
                .build());
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        String msg = engineCallExtApi.buildJob(request);

        if (StringUtils.isEmpty(msg)) {
            // ?????????
            log.info("??????{},??????{},??????{}????????????????????????Job???????????????job????????????", sceneId, taskId, customerId);
            // ??????job ???????????? ???????????? ???????????? ?????????????????????  ???
            // ?????????????????????????????????????????????????????????????????????????????????????????????????????????
            asyncService.checkStartedTask(request.getRequest());
        } else {
            // ????????????
            log.info("??????{},??????{},??????{}????????????????????????Job???????????????job????????????", sceneId, taskId, customerId);
            sceneManageService.reportRecord(SceneManageStartRecordVO.build(sceneId, taskId, customerId).success(false)
                .errorMsg("????????????job??????????????????????????????" + msg).build());
        }
    }

    @Override
    public void initScheduleCallback(ScheduleInitParamExt param) {

    }

    /**
     * ???????????????
     * ?????????????????????????????????redis??????, ???????????????????????????????????????????????????
     */
    private void push(ScheduleStartRequestExt request) {
        //?????????????????????
        String key = ScheduleConstants.getFileSplitQueue(request.getSceneId(), request.getTaskId(), request.getTenantId());
        // ????????????
        List<String> numList = IntStream.rangeClosed(1, request.getTotalIp())
            .boxed().map(String::valueOf)
            .collect(Collectors.toCollection(ArrayList::new));
        // ????????????Redis
        stringRedisTemplate.opsForList().leftPushAll(key, numList);
    }

    /**
     * ????????????????????? ???????????? job configMap
     */
    @IntrestFor(event = "finished")
    public void doDeleteJob(Event event) {
        log.info("??????deleteJob????????? ?????????????????????.....");
        try {
            Object object = event.getExt();
            TaskResult taskResult = (TaskResult)object;
            // ?????? ????????????
            String jobName = ScheduleConstants.getScheduleName(taskResult.getSceneId(), taskResult.getTaskId(),
                taskResult.getTenantId());
            String engineInstanceRedisKey = PressureInstanceRedisKey.getEngineInstanceRedisKey(taskResult.getSceneId(), taskResult.getTaskId(),
                taskResult.getTenantId());
            ScheduleStopRequestExt scheduleStopRequest = new ScheduleStopRequestExt();
            scheduleStopRequest.setJobName(jobName);
            scheduleStopRequest.setEngineInstanceRedisKey(engineInstanceRedisKey);

            EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
            engineCallExtApi.deleteJob(scheduleStopRequest);

            redisTemplate.expire(engineInstanceRedisKey, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("???????????????{}???,??????????????????????????????????????? --> ???deleteJob?????????finished????????????: {}",
                TakinCloudExceptionEnum.TASK_STOP_DELETE_TASK_ERROR, e);
        }

    }

    /**
     * ??????????????????
     */
    public class SceneStopThread implements Runnable {

        private final ScheduleStopRequestExt request;

        public SceneStopThread(ScheduleStopRequestExt request) {
            this.request = request;
        }

        @Override
        public void run() {
            String scheduleName = ScheduleConstants.getScheduleName(request.getSceneId(), request.getTaskId(), request.getTenantId());
            stringRedisTemplate.opsForValue().set(
                ScheduleConstants.FORCE_STOP_POD + scheduleName,
                Boolean.TRUE.toString(), 1, TimeUnit.DAYS);
            {
                try {
                    Thread.sleep(3 * 60 * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log.info("stop wait error :{}", e.getMessage());
                }
                // ??????????????????
                SceneManageQueryOpitons options = new SceneManageQueryOpitons();
                options.setIncludeBusinessActivity(true);
                SceneManageWrapperOutput dto = sceneManageService.getSceneManage(request.getSceneId(), options);
                if (!SceneManageStatusEnum.WAIT.getValue().equals(dto.getStatus())) {
                    // ??????????????????
                    reportService.forceFinishReport(request.getTaskId());
                    Event event = new Event();
                    event.setEventName("??????job");
                    TaskResult taskResult = new TaskResult();
                    taskResult.setSceneId(request.getSceneId());
                    taskResult.setTaskId(request.getTaskId());
                    taskResult.setTenantId(request.getTenantId());
                    event.setExt(taskResult);
                    doDeleteJob(event);
                }
            }
        }
    }
}
