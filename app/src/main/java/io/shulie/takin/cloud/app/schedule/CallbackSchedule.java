package io.shulie.takin.cloud.app.schedule;

import com.github.pagehelper.PageInfo;
import io.shulie.takin.cloud.app.entity.CallbackEntity;
import io.shulie.takin.cloud.app.service.CallbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * ClassName:    CallbackSchedule
 * Package:    io.shulie.takin.cloud.app.schedule
 * Description: 向web服务回调锁定/释放/启动/停止信息
 * Datetime:    2022/6/9   17:20
 * Author:   chenhongqiao@shulie.com
 */
@Slf4j
public class CallbackSchedule implements Runnable {

    private CallbackService callbackService;

    public CallbackSchedule(CallbackService callbackService) {
        this.callbackService = callbackService;
    }

    @Override
    public void run() {
        PageInfo<CallbackEntity> ready = callbackService.listNotCompleted(1, 100);
        log.info("开始调度.共{}条,本次计划调度{}条", ready.getTotal(), ready.getSize());
        for (int i = 0; i < ready.getSize(); i++) {
            CallbackEntity entity = ready.getList().get(i);
            String callbackContent = new String(entity.getContext(), StandardCharsets.UTF_8);
            boolean callback = callbackService.callback(entity.getId(), entity.getUrl(), callbackContent);
            log.info("回调定时器调度内容：{},调度结果：{}", callbackContent, callback);
        }

    }
}
