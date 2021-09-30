package io.shulie.takin.cloud.biz.service.schedule.impl;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import com.pamirs.takin.entity.dao.schedule.TScheduleRecordMapper;
import com.pamirs.takin.entity.domain.entity.schedule.ScheduleRecord;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageStartRecordVO;
import io.shulie.takin.cloud.biz.convertor.ScheduleConvertor;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.service.async.AsyncService;
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
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.common.utils.EnginePluginUtils;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.annotation.IntrestFor;
import io.shulie.takin.ext.api.EngineCallExtApi;
import io.shulie.takin.ext.content.enginecall.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 莫问
 * @date 2020-05-12
 */
@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private StrategyConfigService strategyConfigService;

    @Resource
    private TScheduleRecordMapper TScheduleRecordMapper;

    @Autowired
    private ScheduleEventService scheduleEvent;

    @Autowired
    private SceneManageService sceneManageService;

    @Resource
    private ScheduleRecordEnginePluginService scheduleRecordEnginePluginService;

    @Autowired
    private RedisClientUtils redisClientUtils;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    @Qualifier("stopThreadPool")
    protected ThreadPoolExecutor stopExecutor;

    @Autowired
    private ReportService reportService;

    @Autowired
    private EnginePluginUtils pluginUtils;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void startSchedule(ScheduleStartRequestExt request) {
        log.info("启动调度, 请求数据：{}", request);
        //任务只处理一次
        ScheduleRecord schedule = TScheduleRecordMapper.getScheduleByTaskId(request.getTaskId());
        if (schedule != null) {
            log.error("异常代码【{}】,异常内容：启动调度失败 --> 调度任务[{}]已经启动",
                TakinCloudExceptionEnum.SCHEDULE_START_ERROR, request.getTaskId());
            return;
        }
        //获取策略
        StrategyConfigExt config = strategyConfigService.getDefaultStrategyConfig();
        if (config == null) {
            log.error("异常代码【{}】,异常内容：启动调度失败 --> 调度策略未配置",
                TakinCloudExceptionEnum.SCHEDULE_START_ERROR);
            return;
        }

        //保存调度记录
        ScheduleRecord scheduleRecord = new ScheduleRecord();
        scheduleRecord.setCpuCoreNum(config.getCpuNum());
        scheduleRecord.setPodNum(request.getTotalIp());
        scheduleRecord.setMemorySize(config.getMemorySize());
        scheduleRecord.setSceneId(request.getSceneId());
        scheduleRecord.setTaskId(request.getTaskId());
        scheduleRecord.setStatus(ScheduleConstants.SCHEDULE_STATUS_1);

        scheduleRecord.setTenantId(request.getTenantId());
        scheduleRecord.setPodClass(
            ScheduleConstants.getScheduleName(request.getSceneId(), request.getTaskId(), request.getTenantId()));
        TScheduleRecordMapper.insertSelective(scheduleRecord);

        //add by lipeng 保存调度对应压测引擎插件记录信息
        scheduleRecordEnginePluginService.saveScheduleRecordEnginePlugins(
            scheduleRecord.getId(), request.getEnginePluginsFilePath());
        //add end

        //发布事件
        ScheduleRunRequest eventRequest = new ScheduleRunRequest();
        eventRequest.setScheduleId(scheduleRecord.getId());
        eventRequest.setRequest(request);
        eventRequest.setStrategyConfig(ScheduleConvertor.INSTANCE.ofStrategyConfig(config));
        //把数据放入缓存，初始化回调调度需要
        redisClientUtils.setString(
            ScheduleConstants.getScheduleName(request.getSceneId(), request.getTaskId(), request.getTenantId()),
            JSON.toJSONString(eventRequest));
        // 需要将 本次调度 pod数量存入redis,报告中用到
        // 总计 报告生成用到 调度期间出现错误，这份数据只存24小时
        redisClientUtils.set(
            ScheduleConstants.getPressureNodeTotalKey(request.getSceneId(), request.getTaskId(), request.getTenantId()),
            request.getTotalIp(), 24 * 60 * 60 * 1000);
        //调度初始化
        scheduleEvent.initSchedule(eventRequest);
    }

    @Override
    public void stopSchedule(ScheduleStopRequestExt request) {
        log.info("停止调度, 请求数据：{}", request);
        ScheduleRecord scheduleRecord = TScheduleRecordMapper.getScheduleByTaskId(request.getTaskId());
        if (scheduleRecord != null) {
            // 增加中断
            String scheduleName = ScheduleConstants.getScheduleName(request.getSceneId(), request.getTaskId(), request.getTenantId());
            boolean flag = redisClientUtils.set(ScheduleConstants.INTERRUPT_POD + scheduleName, true, 24 * 60 * 60 * 1000);
            if (flag && !Boolean.parseBoolean(redisClientUtils.getString(ScheduleConstants.FORCE_STOP_POD + scheduleName))) {
                // 3分钟没有停止成功 ，将强制停止
                stopExecutor.execute(new SceneStopThread(request));
            }
        }

    }

    @Override
    public void runSchedule(ScheduleRunRequest request) {
        // 压力机数目记录
        push(request.getRequest());

        // 场景生命周期更新 启动中(文件拆分完成) ---> 创建Job中
        sceneManageService.updateSceneLifeCycle(
            UpdateStatusBean.build(request.getRequest().getSceneId(),
                    request.getRequest().getTaskId(),
                    request.getRequest().getTenantId()).checkEnum(
                    SceneManageStatusEnum.STARTING, SceneManageStatusEnum.FILESPLIT_END)
                .updateEnum(SceneManageStatusEnum.JOB_CREATEING)
                .build());
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        String msg = engineCallExtApi.buildJob(request);

        if (StringUtils.isEmpty(msg)) {
            // 是空的
            log.info("场景{},任务{},顾客{}开始创建压测引擎Job，压测引擎job创建成功", request.getRequest().getSceneId(),
                request.getRequest().getTaskId(),
                request.getRequest().getTenantId());
            // 创建job 开始监控 压力节点 启动情况 起一个线程监控  。
            // 启动检查压力节点启动线程，在允许时间内压力节点未启动完成，主动停止任务
            asyncService.checkStartedTask(request.getRequest());
        } else {
            // 创建失败
            log.info("场景{},任务{},顾客{}开始创建压测引擎Job，压测引擎job创建失败", request.getRequest().getSceneId(),
                request.getRequest().getTaskId(),
                request.getRequest().getTenantId());
            sceneManageService.reportRecord(SceneManageStartRecordVO.build(request.getRequest().getSceneId(),
                    request.getRequest().getTaskId(),
                    request.getRequest().getTenantId()).success(false)
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

        List<String> numList = Lists.newArrayList();
        for (int i = 1; i <= request.getTotalIp(); i++) {
            numList.add(i + "");
        }

        redisClientUtils.leftPushAll(key, numList);
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
            boolean flag = redisClientUtils.set(ScheduleConstants.FORCE_STOP_POD + scheduleName, true, 24 * 60 * 60 * 1000);
            if (flag) {
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
