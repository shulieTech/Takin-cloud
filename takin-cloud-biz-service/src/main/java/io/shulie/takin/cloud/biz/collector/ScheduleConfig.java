package io.shulie.takin.cloud.biz.collector;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.context.annotation.Configuration;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-27 12:06
 */
@Configuration
public class ScheduleConfig {

    @Bean(name = "collectorSchedulerPool")
    public TaskScheduler scheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("collector-scheduler-thread-%d")
            .daemon(true).build();
        taskScheduler.setThreadFactory(threadFactory);
        taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return taskScheduler;
    }

    @Bean(name = "checkStartedPodPool")
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(3);
        // 设置最大线程数
        executor.setMaxPoolSize(20);
        //配置队列大小
        executor.setQueueCapacity(100);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("check-started-pressure-node");
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //执行初始化
        executor.initialize();
        return executor;
    }

    @Bean(name = "updateStatusPool")
    public Executor updateSceneRunningStatusExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(3);
        // 设置最大线程数
        executor.setMaxPoolSize(20);
        //配置队列大小
        executor.setQueueCapacity(100);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("check-started-pressure-node");
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //执行初始化
        executor.initialize();
        return executor;
    }
}
