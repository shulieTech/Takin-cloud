package io.shulie.takin.cloud.app.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务 - 数据校准
 * Scheduly
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Configuration
@EnableScheduling
public class DataCalibration {

    @Scheduled(fixedDelay = 5)
    public void exec() {
        throw new UnsupportedOperationException();
    }
}
