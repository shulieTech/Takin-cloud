package io.shulie.takin.cloud.common.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author xr.l
 *
 * 公用线程池
 */
public class ThreadExecutorPools {

    public static final ExecutorService COMMON_EXECUTOR;

    static {
        COMMON_EXECUTOR = new ThreadPoolExecutor(50, 100, 60L,
            TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
            new ThreadFactoryBuilder().setNameFormat("Common_Thread_%s").build(), new AbortPolicy());
    }

}
