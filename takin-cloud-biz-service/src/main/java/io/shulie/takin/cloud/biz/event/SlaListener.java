package io.shulie.takin.cloud.biz.event;

import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.annotation.IntrestFor;
import io.shulie.takin.cloud.biz.service.sla.SlaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author qianshui
 * @date 2020/4/20 下午8:19
 */
@Component
@Slf4j
public class SlaListener {

    @Autowired
    private SlaService slaService;

    /**
     * 任务启动，缓存值
     *
     * @param event -
     */
    @IntrestFor(event = "started")
    public void doStartSlaEvent(Event event) {
        log.info("SLA配置，从调度中心收到压测任务启动成功事件");
        Object object = event.getExt();
        TaskResult taskBean = (TaskResult)object;
        slaService.cacheData(taskBean.getSceneId());
    }

    /**
     * 任务停止，清除缓存
     *
     * @param event -
     */
    @IntrestFor(event = "finished")
    public void doStopSlaEvent(Event event) {
        log.info("通知SLA配置模块，从调度中心收到压测任务结束事件");
        try {
            Object object = event.getExt();
            TaskResult taskResult = (TaskResult)object;
            slaService.removeMap(taskResult.getSceneId());
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：结束任务sla错误 --> 【SLA】处理finished事件异常: {}",
                    TakinCloudExceptionEnum.TASK_STOP_SAL_FINISH_ERROR,e);
        }
    }

    @IntrestFor(event = "metricsData")
    public void doMetricsData(Event event) {
        try {
            Object object = event.getExt();
            SendMetricsEvent metricsEvnet = (SendMetricsEvent)object;
            log.info("收到数据采集发来Metrics数据，timestamp = {}", metricsEvnet.getTimestamp());
            slaService.buildWarn(metricsEvnet);
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：运行中任务sla采集数据错误 --> 【SLA】metricsData事件异常: {}",
                    TakinCloudExceptionEnum.TASK_STOP_SAL_FINISH_ERROR,e);
        }
    }
}

