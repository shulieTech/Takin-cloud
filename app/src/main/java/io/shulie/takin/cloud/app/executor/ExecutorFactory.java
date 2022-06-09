package io.shulie.takin.cloud.app.executor;

import java.util.concurrent.*;

/**
 * ClassName:    ExecutorFactory
 * Package:    io.shulie.takin.lite.pressure.api.executor
 * Description:
 * Datetime:    2022/1/19   2:07 下午
 * Author:   chenhongqiao@shulie.com
 */
@SuppressWarnings({"PMD.ThreadPoolCreationRule", "checkstyle:overloadmethodsdeclarationorder",
    "checkstyle:missingjavadocmethod"})
public final class ExecutorFactory {

    public static ExecutorService newSingleExecutorService() {
        return Executors.newFixedThreadPool(1);
    }

    public static ExecutorService newSingleExecutorService(final ThreadFactory threadFactory) {
        return Executors.newFixedThreadPool(1, threadFactory);
    }

    public static ExecutorService newFixedExecutorService(final int nThreads) {
        return Executors.newFixedThreadPool(nThreads);
    }

    public static ExecutorService newFixedExecutorService(final int nThreads, final ThreadFactory threadFactory) {
        return Executors.newFixedThreadPool(nThreads, threadFactory);
    }

    public static ScheduledExecutorService newSingleScheduledExecutorService(final ThreadFactory threadFactory) {
        return Executors.newScheduledThreadPool(1, threadFactory);
    }

    public static ScheduledExecutorService newScheduledExecutorService(final int nThreads,
        final ThreadFactory threadFactory) {
        return Executors.newScheduledThreadPool(nThreads, threadFactory);
    }

    public static ThreadPoolExecutor newCustomerThreadExecutor(final int coreThreads, final int maxThreads,
        final long keepAliveTimeMs, final ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(coreThreads, maxThreads, keepAliveTimeMs, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), threadFactory);
    }

    public static final class Managed {

        private static final String DEFAULT_NAMESPACE = "nacos";

        private static final ThreadPoolManager THREAD_POOL_MANAGER = ThreadPoolManager.getInstance();

        /**
         * Create a new single executor service with default thread factory and register to manager.
         *
         * @param group group name
         * @return new single executor service
         */
        public static ExecutorService newSingleExecutorService(final String group) {
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            THREAD_POOL_MANAGER.register(DEFAULT_NAMESPACE, group, executorService);
            return executorService;
        }

        /**
         * Create a new single executor service with input thread factory and register to manager.
         *
         * @param group         group name
         * @param threadFactory thread factory
         * @return new single executor service
         */
        public static ExecutorService newSingleExecutorService(final String group, final ThreadFactory threadFactory) {
            ExecutorService executorService = Executors.newFixedThreadPool(1, threadFactory);
            THREAD_POOL_MANAGER.register(DEFAULT_NAMESPACE, group, executorService);
            return executorService;
        }

        /**
         * Create a new fixed executor service with default thread factory and register to manager.
         *
         * @param group    group name
         * @param nThreads thread number
         * @return new fixed executor service
         */
        public static ExecutorService newFixedExecutorService(final String group, final int nThreads) {
            ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
            THREAD_POOL_MANAGER.register(DEFAULT_NAMESPACE, group, executorService);
            return executorService;
        }

        /**
         * Create a new fixed executor service with input thread factory and register to manager.
         *
         * @param group         group name
         * @param nThreads      thread number
         * @param threadFactory thread factory
         * @return new fixed executor service
         */
        public static ExecutorService newFixedExecutorService(final String group, final int nThreads,
            final ThreadFactory threadFactory) {
            ExecutorService executorService = Executors.newFixedThreadPool(nThreads, threadFactory);
            THREAD_POOL_MANAGER.register(DEFAULT_NAMESPACE, group, executorService);
            return executorService;
        }

        /**
         * Create a new single scheduled executor service with input thread factory and register to manager.
         *
         * @param group         group name
         * @param threadFactory thread factory
         * @return new single scheduled executor service
         */
        public static ScheduledExecutorService newSingleScheduledExecutorService(final String group,
            final ThreadFactory threadFactory) {
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(8, threadFactory);
            THREAD_POOL_MANAGER.register(DEFAULT_NAMESPACE, group, executorService);
            return executorService;
        }

        /**
         * Create a new scheduled executor service with input thread factory and register to manager.
         *
         * @param group         group name
         * @param nThreads      thread number
         * @param threadFactory thread factory
         * @return new scheduled executor service
         */
        public static ScheduledExecutorService newScheduledExecutorService(final String group, final int nThreads,
            final ThreadFactory threadFactory) {
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(nThreads, threadFactory);
            THREAD_POOL_MANAGER.register(DEFAULT_NAMESPACE, group, executorService);
            return executorService;
        }

        /**
         * Create a new custom executor service and register to manager.
         *
         * @param group           group name
         * @param coreThreads     core thread number
         * @param maxThreads      max thread number
         * @param keepAliveTimeMs keep alive time milliseconds
         * @param threadFactory   thread factory
         * @return new custom executor service
         */
        public static ThreadPoolExecutor newCustomerThreadExecutor(final String group, final int coreThreads,
            final int maxThreads, final long keepAliveTimeMs, final ThreadFactory threadFactory) {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(coreThreads, maxThreads, keepAliveTimeMs,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
            THREAD_POOL_MANAGER.register(DEFAULT_NAMESPACE, group, executor);
            return executor;
        }

    }
}
