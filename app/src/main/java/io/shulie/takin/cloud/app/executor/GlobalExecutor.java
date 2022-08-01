package io.shulie.takin.cloud.app.executor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.annotation.PostConstruct;

import cn.hutool.core.thread.NamedThreadFactory;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

/**
 * 全局线程池
 *
 * @author chenhongqiao@shulie.com
 */
@Component
public class GlobalExecutor {
    @lombok.Getter
    private ScheduledExecutorService executor;
    @Value("${global.executor.size:8}")
    Integer globalExecutorSize;
    @Value("${global.executor.name:GlobalExecutor-}")
    String globalExecutorSizeName;

    @PostConstruct
    public void init() {
        NamedThreadFactory threadFactory = new NamedThreadFactory(globalExecutorSizeName, false);
        executor = new ScheduledThreadPoolExecutor(globalExecutorSize, threadFactory);
    }
}
