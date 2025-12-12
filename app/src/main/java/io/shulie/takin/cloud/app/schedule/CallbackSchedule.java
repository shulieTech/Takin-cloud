package io.shulie.takin.cloud.app.schedule;

import lombok.extern.slf4j.Slf4j;

import com.github.pagehelper.PageInfo;

import io.shulie.takin.cloud.data.entity.CallbackEntity;
import io.shulie.takin.cloud.app.service.CallbackService;

/**
 * 回调通知
 *
 * @author chenhongqiao@shulie.com
 */
@Slf4j(topic = "CALLBACK")
public class CallbackSchedule implements Runnable {

    private final CallbackService callbackService;

    public CallbackSchedule(CallbackService callbackService) {
        this.callbackService = callbackService;
    }

    @Override
    public void run() {
        try {
            long firstTime = System.currentTimeMillis();
            PageInfo<CallbackEntity> ready = callbackService.listNotCompleted(1, 100);
            long beginTime = System.currentTimeMillis();
            log.info("开始调度.共{}条,本次计划调度{}条,查询耗时{}ms", ready.getTotal(), ready.getSize(), beginTime -firstTime);
            if(ready.getSize() == 0) {
                return;
            }
            ready.getList().forEach(t -> {
                try {
                    long startTime = System.currentTimeMillis();
                    callbackService.callback(t.getId(), t.getUrl(), t.getType(), t.getContext());
                    long costTime = System.currentTimeMillis() - startTime;
                    if(costTime > 1000) {
                        log.warn("回调定时器调度了一条:{}，url:{}, 耗时:{}ms", t.getId(), t.getUrl(), costTime);
                    }
                } catch (RuntimeException e) {
                    log.error("调度id={}异常\n", t.getId() , e);
                }
            });
            log.info("调度{}条完成.共耗时{}ms", ready.getSize(), System.currentTimeMillis() - beginTime);
        } catch (RuntimeException e) {
            log.error("单次调度异常\n", e);
        }
    }
}
