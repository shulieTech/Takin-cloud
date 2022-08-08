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
            PageInfo<CallbackEntity> ready = callbackService.listNotCompleted(1, 100);
            log.info("开始调度.共{}条,本次计划调度{}条", ready.getTotal(), ready.getSize());
            ready.getList().forEach(t -> {
                try {
                    callbackService.callback(t.getId(), t.getUrl(), t.getType(), t.getContext());
                    log.info("回调定时器调度了一条:{}", t.getId());
                } catch (RuntimeException e) {
                    log.error("调度异常\n", e);
                }
            });
        } catch (RuntimeException e) {
            log.error("单次调度异常\n", e);
        }
    }
}
