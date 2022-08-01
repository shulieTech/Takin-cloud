package io.shulie.takin.cloud.app.conf;

import javax.annotation.Resource;
import javax.annotation.PostConstruct;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import io.shulie.takin.cloud.app.executor.GlobalExecutor;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.app.service.ExcessJobService;
import io.shulie.takin.cloud.app.schedule.CallbackSchedule;
import io.shulie.takin.cloud.app.schedule.CalibrationSchedule;

/**
 * 调度配置
 *
 * @author chenhongqiao@shulie.com
 */
@Configuration
public class ScheduleConfig {
    @Resource
    private GlobalExecutor globalExecutor;
    @Resource
    private CallbackService callbackService;
    @Resource
    private ExcessJobService excessJobService;

    @Value("${schedule.delay.initial:0}")
    Integer initialDelay;

    @Value("${schedule.delay.size:5000}")
    Integer delay;

    @PostConstruct
    public void init() {
        TimeUnit unit = TimeUnit.MILLISECONDS;

        globalExecutor.getExecutor().scheduleWithFixedDelay(new CallbackSchedule(callbackService), initialDelay, delay, unit);
        globalExecutor.getExecutor().scheduleWithFixedDelay(new CalibrationSchedule(excessJobService), initialDelay, delay, unit);
    }
}
