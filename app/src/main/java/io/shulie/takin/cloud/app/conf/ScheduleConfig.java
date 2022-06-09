package io.shulie.takin.cloud.app.conf;

import io.shulie.takin.cloud.app.executor.GlobalExecutor;
import io.shulie.takin.cloud.app.schedule.CalibrationSchedule;
import io.shulie.takin.cloud.app.schedule.CallbackSchedule;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.app.service.ExcessJobService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static io.shulie.takin.cloud.constant.ScheduleConstant.SCHEDULE_CALLBACK_EXEC_DELAY_TIME;
import static io.shulie.takin.cloud.constant.ScheduleConstant.SCHEDULE_INIT_EXEC_DELAY_TIME;

/**
 * ClassName:    ScheduleConfig
 * Package:    io.shulie.takin.cloud.app.conf
 * Description:
 * Datetime:    2022/6/9   21:00
 * Author:   chenhongqiao@shulie.com
 */
//@DependsOn({"callbackService", "excessJobService"})
@Configuration
public class ScheduleConfig {

    @Resource
    private CallbackService callbackService;

    @Resource
    private ExcessJobService excessJobService;

    @PostConstruct
    public void init(){
        GlobalExecutor.schedule(new CallbackSchedule(callbackService),SCHEDULE_INIT_EXEC_DELAY_TIME,
                SCHEDULE_CALLBACK_EXEC_DELAY_TIME,
                TimeUnit.MILLISECONDS);

        GlobalExecutor.schedule(new CalibrationSchedule(excessJobService),SCHEDULE_INIT_EXEC_DELAY_TIME,
                SCHEDULE_CALLBACK_EXEC_DELAY_TIME,
                TimeUnit.MILLISECONDS);
    }
}
