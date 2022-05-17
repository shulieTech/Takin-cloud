package io.shulie.takin.cloud.app.schedule;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

import javax.annotation.PostConstruct;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.github.pagehelper.PageInfo;

import io.shulie.takin.cloud.app.entity.CallbackEntity;
import io.shulie.takin.cloud.app.service.CallbackService;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 回调内容的调度
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Component
@Slf4j(topic = "CALLBACK")
public class Callback {
    @Value("${callback.thread.pool.size:10}")
    Integer threadPoolSize;
    private ThreadPoolExecutor threadpool;
    @javax.annotation.Resource
    private CallbackService callbackService;
    private final LinkedHashMap<Long, Boolean> cacheData = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        threadpool = new ThreadPoolExecutor(
            threadPoolSize, threadPoolSize,
            0, TimeUnit.DAYS,
            new ArrayBlockingQueue<>(threadPoolSize),
            t -> new Thread(t, "调度所属线程组(回调)"),
            new ThreadPoolExecutor.AbortPolicy());
    }

    @Scheduled(fixedRate = 1000)
    public void callback() {
        try {
            PageInfo<CallbackEntity> ready = callbackService.listNotCompleted(1, 10);
            log.info("开始调度.共{}条,本次计划调度{}条", ready.getTotal(), ready.getSize());
            for (int i = 0; i < ready.getSize(); i++) {
                CallbackEntity entity = ready.getList().get(i);
                // 缓存校验 存在 并且是 False
                if (cacheData.containsKey(entity.getId()) && !Boolean.FALSE.equals(cacheData.get(entity.getId()))) {
                    log.warn("第{}条在缓存中了.\n{}", (i + 1), entity);
                    continue;
                }
                // 提交到线程池运行
                submitToPool(new Exec(entity, callbackService, cacheData), i + 1);
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
    public static class Exec implements Runnable {
        private final CallbackEntity entity;
        private final CallbackService service;
        private final Map<Long, Boolean> cache;

        Exec(CallbackEntity callbackEntity, CallbackService callbackService, Map<Long, Boolean> cacheData) {
            this.cache = cacheData;
            this.entity = callbackEntity;
            this.service = callbackService;
        }

        @Override

        public void run() {
            cache.put(entity.getId(), null);
            // 预插入Log
            Long callbackLogId;
            try {
                callbackLogId = service.createLog(entity.getId(), entity.getUrl(), entity.getContext());
            } catch (Exception e) {
                log.warn("预创建回调日志失败.\n{}", entity);
                cache.remove(entity.getId());
                return;
            }
            // 开始执行回调
            byte[] responseData;
            try {
                // 组装请求
                HttpRequest request = HttpUtil.createPost(entity.getUrl());
                request.contentType(ContentType.JSON.getValue());
                request.setConnectionTimeout(3000).body(entity.getContext());
                // 接收相应
                try (HttpResponse response = request.execute()) {
                    responseData = response.bodyBytes();
                }
                // 记录日志
                boolean completed = service.fillLog(callbackLogId, responseData);
                // 更新缓存
                cache.put(entity.getId(), completed);
            } catch (Exception e) {
                log.error("单次过程失败.\n", e);
                service.fillLog(callbackLogId, ("Exception:\n" + e.getMessage()).getBytes(StandardCharsets.UTF_8));
                cache.put(entity.getId(), false);
            }
        }
    }
}
