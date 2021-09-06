package io.shulie.takin.eventcenter.publish;

import com.alibaba.fastjson.JSON;

import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fanxx
 * @date 2020/4/17 下午8:28
 */
@Component
public class EventPublish {
    private static final Logger logger = LoggerFactory.getLogger(EventPublish.class);

    @Autowired
    private EventCenterTemplate eventCenterTemplate;

    public void finished(TaskResult taskResult) {
        Event event = new Event();
        event.setEventName("finished");
        event.setExt(taskResult);
        eventCenterTemplate.doEvents(event);
        logger.info("EventPublish event published: finished,{}", JSON.toJSONString(event));
    }

    public void started(TaskResult taskResult) {
        Event event = new Event();
        event.setEventName("started");
        event.setExt(taskResult);
        eventCenterTemplate.doEvents(event);
        logger.info("EventPublish event published: started,{}", JSON.toJSONString(event));
    }

    public void failed(TaskResult taskResult) {
        Event event = new Event();
        event.setEventName("failed");
        event.setExt(taskResult);
        eventCenterTemplate.doEvents(event);
        logger.info("EventPublish event published: failed,{}", JSON.toJSONString(event));
    }
}
