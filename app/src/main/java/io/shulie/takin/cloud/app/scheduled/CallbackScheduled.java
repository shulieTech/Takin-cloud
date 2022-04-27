package io.shulie.takin.cloud.app.scheduled;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 回调内容的调度
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j(topic = "callback")
@Component
public class CallbackScheduled {
    @Scheduled(fixedRate = 1000)
    public void callback() {
        log.info("定时回调-INFO");
        log.warn("定时回调-WARN");
        log.error("定时回调-ERROR");
    }
}
