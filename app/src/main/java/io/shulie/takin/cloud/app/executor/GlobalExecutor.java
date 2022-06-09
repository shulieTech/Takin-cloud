package io.shulie.takin.cloud.app.executor;

import org.apache.commons.lang3.ClassUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:    GlobalExecutor
 * Package:    io.shulie.takin.drilling.sdk.executor
 * Description:
 * Datetime:    2022/1/19   2:07 下午
 * Author:   chenhongqiao@shulie.com
 */
public final class GlobalExecutor {

    private static final ScheduledExecutorService GLOBAL_EXECUTOR_SERVICE = ExecutorFactory.Managed
        .newSingleScheduledExecutorService(ClassUtils.getCanonicalName(GlobalExecutor.class),
            new NameThreadFactory("io.shulie.takin.cloud.app.executor"));

    public static ScheduledFuture<?> schedule(Runnable runnable, long initialDelay, long delay,
        TimeUnit unit) {
        return GLOBAL_EXECUTOR_SERVICE.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
    }

    public static ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
        return GLOBAL_EXECUTOR_SERVICE.schedule(runnable, delay, unit);
    }

    public static void execute(Runnable runnable) {
        GLOBAL_EXECUTOR_SERVICE.execute(runnable);
    }

    public static ExecutorService getGlobalExecutorService() {
        return GLOBAL_EXECUTOR_SERVICE;
    }
}
