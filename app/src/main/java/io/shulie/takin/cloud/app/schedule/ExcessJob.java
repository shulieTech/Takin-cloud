package io.shulie.takin.cloud.app.schedule;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.RejectedExecutionException;

import javax.annotation.Resource;
import javax.annotation.PostConstruct;

import org.redisson.api.RLock;
import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.PageInfo;
import org.redisson.api.RedissonClient;
import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import io.shulie.takin.cloud.constant.Message;
import io.shulie.takin.cloud.app.entity.ExcessJobEntity;
import io.shulie.takin.cloud.app.service.ExcessJobService;

/**
 * 调度 - 额外的任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Component
@Slf4j(topic = "EXCESS-JOB")
public class ExcessJob {
    @Value("${schedule.thread.pool.size:10}")
    Integer threadPoolSize;
    @Resource
    RedissonClient redissonClient;
    @Resource
    ExcessJobService excessJobService;
    /**
     * 线程池
     */
    private ThreadPoolExecutor threadpool;

    @PostConstruct
    public void init() {
        threadpool = new ThreadPoolExecutor(
            threadPoolSize, threadPoolSize,
            0, TimeUnit.DAYS,
            new ArrayBlockingQueue<>(threadPoolSize),
            t -> new Thread(t, "调度所属线程组(定时任务)"),
            new ThreadPoolExecutor.AbortPolicy());
    }

    @Scheduled(fixedDelay = 1000)
    public void exec() {
        try {
            // 分页查询
            PageInfo<ExcessJobEntity> ready = excessJobService.listNotCompleted(1, 10, null);
            log.info("开始调度.共{}条,本次计划调度{}条\n{}", ready.getTotal(), ready.getSize(), ready);
            for (int i = 0; i < ready.getSize(); i++) {
                ExcessJobEntity entity = ready.getList().get(i);
                RLock locker = redissonClient.getLock(CharSequenceUtil.format(Message.SCHEDULE_LOCK_KEY, entity.getId()));
                //  提交到线程池运行 || 跳过
                if (locker.tryLock()) {
                    // 提交到线程池运行
                    submitToPool(new Exec(entity, excessJobService, locker), i + 1);
                } else {
                    log.warn("第{}条已经在运行了.\n{}", (i + 1), entity);
                }
            }
        } catch (Exception e) {
            log.error("定时过程失败.\n", e);
        }
    }

    /**
     * 提交到线程池
     *
     * @param exec 执行对象
     */
    private void submitToPool(Exec exec, int index) {
        try {
            threadpool.submit(exec);
        } catch (RejectedExecutionException ex) {
            log.warn("第{}条被线程池拒绝", index);
        }
    }

    /**
     * 执行过程
     */
    protected static class Exec implements Runnable {
        private final ExcessJobService service;
        private final ExcessJobEntity entity;
        private final RLock locker;

        Exec(ExcessJobEntity excessJobEntity, ExcessJobService excessJobService, RLock locker) {
            this.service = excessJobService;
            this.entity = excessJobEntity;
            this.locker = locker;
        }

        @Override
        public void run() {
            try {
                service.exec(entity);
            } catch (Exception e) {
                log.error("发生异常.\n", e);
            } finally {
                locker.unlock();
            }

        }
    }

}
