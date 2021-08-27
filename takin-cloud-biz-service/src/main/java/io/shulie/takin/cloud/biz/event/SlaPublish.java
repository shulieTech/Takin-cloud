package io.shulie.takin.cloud.biz.event;

import io.shulie.takin.ext.content.enginecall.ScheduleStopRequestExt;
import io.shulie.takin.cloud.common.constants.ScheduleEventConstant;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: qianshui
 * @Date: 2020/4/17 下午8:28
 * @Description:
 */
@Component
@Slf4j
public class SlaPublish {

    @Autowired
    private EventCenterTemplate eventCenterTemplate;

    public void stop(ScheduleStopRequestExt scheduleStopRequest) {
        try {
            Event event = new Event();
            event.setEventName(ScheduleEventConstant.STOP_SCHEDULE_EVENT);
            event.setExt(scheduleStopRequest);
            eventCenterTemplate.doEvents(event);
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：压测任务停止异常 --> 【SLA】发起stop动作异常: {},入参:{}",
                    TakinCloudExceptionEnum.TASK_RUNNING_SAL_METRICS_DATA_ERROR,e,scheduleStopRequest.toString());
        }
    }
}
