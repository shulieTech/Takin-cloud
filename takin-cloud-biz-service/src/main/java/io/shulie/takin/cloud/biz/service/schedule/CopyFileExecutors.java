package io.shulie.takin.cloud.biz.service.schedule;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author mubai
 * @date 2020-10-29 17:21
 */
@Component
public class CopyFileExecutors implements DisposableBean {

    public static ExecutorService poll = null;

    @PostConstruct
    public void init() {
        Executors.newFixedThreadPool(2);
    }

    @Override
    public void destroy() {
        poll.shutdown();
    }
}
