package io.shulie.takin.cloud.biz.message.listener;

import com.google.common.collect.Lists;
import io.shulie.jmeter.tool.redis.domain.TkMessage;
import io.shulie.takin.cloud.biz.message.domain.EngineEventInfoBo;
import io.shulie.takin.cloud.biz.service.pressure.PressureTaskService;
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

    @Override
    public List<String> getTags() {
        return Lists.newArrayList("event");
    }

    @Override
    public boolean receive(TkMessage message) {
        try {
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
            PressureTaskEntity update = new PressureTaskEntity();
            update.setId(eventInfo.getTaskId());
            update.setStatus(eventInfo.getEvent().getCode());
            pressureTaskService.update(update);
        } catch (Throwable t) {
            log.error("revice message error!message="+JsonUtil.toJson(message));
        }
        return true;
    }
}
