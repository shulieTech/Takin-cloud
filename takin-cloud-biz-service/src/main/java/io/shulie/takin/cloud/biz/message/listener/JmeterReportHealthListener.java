package io.shulie.takin.cloud.biz.message.listener;

import com.google.common.collect.Lists;
import io.shulie.jmeter.tool.redis.domain.TkMessage;
import io.shulie.takin.cloud.biz.message.domain.EngineHealthDataBo;
import io.shulie.takin.cloud.biz.service.pressure.PressureTaskService;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;

/**
 * @Author: liyuanba
 * @Date: 2022/1/28 2:34 下午
 */
@Slf4j
@Component
public class JmeterReportHealthListener extends AbstractJmeterReportListener {
    @Resource
    private PressureTaskService pressureTaskService;

    @Override
    public List<String> getTags() {
        return Lists.newArrayList("health");
    }

    @Override
    public boolean receive(TkMessage message) {
        EngineHealthDataBo healthData = JsonUtil.parseObject(message.getContent(), EngineHealthDataBo.class);
        PressureSceneEnum sceneType = PressureSceneEnum.value(healthData.getSceneType());
        if (sceneType == PressureSceneEnum.INSPECTION_MODE) {
            PressureTaskEntity task = pressureTaskService.getById(healthData.getTaskId());
            if (null == task) {
                log.warn("任务不存在:message="+JsonUtil.toJson(message));
                return true;
            }
            PressureTaskEntity update = new PressureTaskEntity();
            update.setId(healthData.getTaskId());
            update.setGmtLive(Calendar.getInstance().getTime());
            pressureTaskService.update(update);
        }
        return true;
    }
}
