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
 * @author 莫问
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
        log.info("启动调度, 请求数据：{}", request);
        //任务只处理一次
        ScheduleRecord schedule = tScheduleRecordMapper.getScheduleByTaskId(request.getTaskId());
        if (schedule != null) {
            log.error("异常代码【{}】,异常内容：启动调度失败 --> 调度任务[{}]已经启动",
                TakinCloudExceptionEnum.SCHEDULE_START_ERROR, request.getTaskId());
            return;
        }
        //获取策略
        StrategyConfigExt config = strategyConfigService.getCurrentStrategyConfig();
        if (config == null) {
            log.error("异常代码【{}】,异常内容：启动调度失败 --> 调度策略未配置",
                TakinCloudExceptionEnum.SCHEDULE_START_ERROR);
            return;
        }

        String scheduleName = ScheduleConstants.getScheduleName(request.getSceneId(), request.getTaskId(), request.getTenantId());

        //保存调度记录
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

        //add by 李鹏 保存调度对应压测引擎插件记录信息
        scheduleRecordEnginePluginService.saveScheduleRecordEnginePlugins(
            scheduleRecord.getId(), request.getEnginePluginsFilePath());
        //add end

        //发布事件
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
        if (StringUtils.indexOf(appConfig.getEngineRedisClusterAddress(), ",") != -1) {
            //redis集群模式
            eventRequest.setEngineRedisAddress(appConfig.getEngineRedisClusterAddress());
        } else if (StringUtils.isNotBlank(appConfig.getEngineRedisAddress())) {
            //redis单例模式
            eventRequest.setEngineRedisAddress(appConfig.getEngineRedisAddress());
        }
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
        //增加ptl日志上传位置参数
        ptlLogConfig.setPtlUploadFrom(appConfig.getEngineLogUploadModel());
        eventRequest.setPtlLogConfig(ptlLogConfig);

        //把数据放入缓存，初始化回调调度需要
        stringRedisTemplate.opsForValue().set(scheduleName, JSON.toJSONString(eventRequest));
        // 需要将 本次调度 pod数量存入redis,报告中用到
        // 总计 报告生成用到 调度期间出现错误，这份数据只存24小时
        stringRedisTemplate.opsForValue().set(
            ScheduleConstants.getPressureNodeTotalKey(request.getSceneId(), request.getTaskId(), request.getTenantId()),
            String.valueOf(request.getTotalIp()), 1, TimeUnit.DAYS);
        //调度初始化
        scheduleEvent.initSchedule(eventRequest);
    }

    @Override
    public void stopSchedule(ScheduleStopRequestExt request) {
        log.info("停止调度, 请求数据：{}", request);
        ScheduleRecord scheduleRecord = tScheduleRecordMapper.getScheduleByTaskId(request.getTaskId());
        if (scheduleRecord != null) {
            // 增加中断
            String scheduleName = ScheduleConstants.getScheduleName(request.getSceneId(), request.getTaskId(), request.getTenantId());
            stringRedisTemplate.opsForValue().set(
                ScheduleConstants.INTERRUPT_POD + scheduleName,
                Boolean.TRUE.toString(), 1, TimeUnit.DAYS);
            if (!Boolean.parseBoolean(stringRedisTemplate.opsForValue().get(ScheduleConstants.FORCE_STOP_POD + scheduleName))) {
                // 3分钟没有停止成功 ，将强制停止
                stopExecutor.execute(new SceneStopThread(request));
            }
        }

    }

    @Override
    public void runSchedule(ScheduleRunRequest request) {
        ScheduleStartRequestExt startRequest = request.getRequest();
        // 压力机数目记录
        push(startRequest);

        Long sceneId = startRequest.getSceneId();
        Long taskId = startRequest.getTaskId();
        Long customerId = startRequest.getTenantId();

        // 场景生命周期更新 启动中(文件拆分完成) ---> 创建Job中
        sceneManageService.updateSceneLifeCycle(
            UpdateStatusBean.build(sceneId, taskId, customerId)
                .checkEnum(SceneManageStatusEnum.STARTING, SceneManageStatusEnum.FILE_SPLIT_END)
                .updateEnum(SceneManageStatusEnum.JOB_CREATING)
                .build());
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        String msg = engineCallExtApi.buildJob(request);

        if (StringUtils.isEmpty(msg)) {
            // 是空的
            log.info("场景{},任务{},顾客{}开始创建压测引擎Job，压测引擎job创建成功", sceneId, taskId, customerId);
            // 创建job 开始监控 压力节点 启动情况 起一个线程监控  。
            // 启动检查压力节点启动线程，在允许时间内压力节点未启动完成，主动停止任务
            asyncService.checkStartedTask(request.getRequest());
        } else {
            // 创建失败
            log.info("场景{},任务{},顾客{}开始创建压测引擎Job，压测引擎job创建失败:{}", sceneId, taskId, customerId, msg);
            sceneManageService.reportRecord(SceneManageStartRecordVO.build(sceneId, taskId, customerId).success(false)
                .errorMsg("压测引擎job创建失败，失败原因：" + msg).build());
        }
    }

    @Override
    public void initScheduleCallback(ScheduleInitParamExt param) {

    }

    /**
     * 临时方案：
     * 拆分文件的索引都存入到redis队列, 避免控制台集群环境下索引获取不正确
     */
    private void push(ScheduleStartRequestExt request) {
        //把数据放入队列
        String key = ScheduleConstants.getFileSplitQueue(request.getSceneId(), request.getTaskId(), request.getTenantId());
        // 生成集合
        List<String> numList = IntStream.rangeClosed(1, request.getTotalIp())
            .boxed().map(String::valueOf)
            .collect(Collectors.toCollection(ArrayList::new));
        // 集合放入Redis
        stringRedisTemplate.opsForList().leftPushAll(key, numList);
    }

    /**
     * 压测结束，删除 压力节点 job configMap
     */
    @IntrestFor(event = "finished")
    public void doDeleteJob(Event event) {
        log.info("通知deleteJob模块， 监听到完成事件.....");
        try {
            Object object = event.getExt();
            TaskResult taskResult = (TaskResult)object;
            // 删除 压测任务
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
            log.error("异常代码【{}】,异常内容：任务停止失败失败 --> 【deleteJob】处理finished事件异常: {}",
                TakinCloudExceptionEnum.TASK_STOP_DELETE_TASK_ERROR, e);
        }

    }

    /**
     * 场景强制停止
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
                // 查看场景状态
                SceneManageQueryOpitons options = new SceneManageQueryOpitons();
                options.setIncludeBusinessActivity(true);
                SceneManageWrapperOutput dto = sceneManageService.getSceneManage(request.getSceneId(), options);
                if (!SceneManageStatusEnum.WAIT.getValue().equals(dto.getStatus())) {
                    // 触发强制停止
                    reportService.forceFinishReport(request.getTaskId());
                    Event event = new Event();
                    event.setEventName("删除job");
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
