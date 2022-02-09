package io.shulie.takin.cloud.biz.message.listener;

import com.google.common.collect.Lists;
import io.shulie.jmeter.tool.redis.domain.TkMessage;
import io.shulie.takin.cloud.biz.message.domain.EngineEvent;
import io.shulie.takin.cloud.biz.message.domain.EngineEventInfoBo;
import io.shulie.takin.cloud.biz.service.engine.EngineService;
import io.shulie.takin.cloud.biz.service.pressure.PressureTaskService;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: liyuanba
 * @Date: 2022/1/28 2:21 下午
 */
@Slf4j
@Component
public class JmeterReportEventListener extends AbstractJmeterReportListener {
    @Resource
    private PressureTaskService pressureTaskService;
    @Resource
    private EngineService engineService;

    @Override
    public List<String> getTags() {
        return Lists.newArrayList("event");
    }

    @Override
    public boolean receive(TkMessage message) {
        try {
            log.info("event message="+JsonUtil.toJson(message));
            EngineEventInfoBo eventInfo = JsonUtil.parseObject(message.getContent(), EngineEventInfoBo.class);
            if (null == eventInfo) {
                return true;
            }
            if (null == eventInfo.getEvent() || null == eventInfo.getTaskId()) {
                return true;
            }
            PressureSceneEnum sceneType = PressureSceneEnum.value(eventInfo.getSceneType());
            if (sceneType != PressureSceneEnum.INSPECTION_MODE) {
                return true;
            }
            PressureTaskEntity task = pressureTaskService.getById(eventInfo.getTaskId());
            if (null == task) {
                log.warn("任务不存在:message=" + JsonUtil.toJson(message));
                return true;
            }
            if (EngineEvent.START_FAILED == eventInfo.getEvent() || EngineEvent.TEST_END == eventInfo.getEvent()) {
                String jobName = ScheduleConstants.getJobName(sceneType, eventInfo.getSceneId(), eventInfo.getTaskId(), eventInfo.getCustomerId());
                engineService.deleteJob(jobName);
                log.info("delete job: "+jobName);
            } else if (EngineEvent.TEST_START == eventInfo.getEvent()) {
                if (task.getStatus() != 0) {
                    log.warn("任务状态异常：taskId="+eventInfo.getTaskId()+", task.sutatus="+task.getStatus()+", event="+eventInfo.getEvent());
                    return true;
                }
            }
            pressureTaskService.updateStatus(eventInfo.getTaskId(), eventInfo.getEvent().getStatus(), eventInfo.getMessage());
            log.info("update task status, taskId="+eventInfo.getTaskId()+", status="+eventInfo.getEvent().getStatus()+", message="+eventInfo.getMessage());
        } catch (Throwable t) {
            log.error("revice message error!message="+JsonUtil.toJson(message));
        }
        return true;
    }
}
