package io.shulie.takin.cloud.app.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassName:    NameThreadFactory
 * Package:    io.shulie.takin.lite.pressure.api.executor
 * Description:
 * Datetime:    2022/1/18   7:05 下午
 * Author:   chenhongqiao@shulie.com
 */
public class NameThreadFactory implements ThreadFactory {

    private final AtomicInteger id = new AtomicInteger(0);

    private String name;

    public NameThreadFactory(String name) {
        if (!name.endsWith(".")) {
            name += ".";
        }
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        String threadName = name + id.getAndIncrement();
        Thread thread = new Thread(r, threadName);
        thread.setDaemon(true);
        return thread;
    }
}