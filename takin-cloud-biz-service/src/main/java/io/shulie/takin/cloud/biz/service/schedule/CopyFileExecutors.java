package io.shulie.takin.cloud.biz.service.schedule;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: mubai
 * @Date: 2020-10-29 17:21
 * @Description:
 */

@Component
public class CopyFileExecutors implements DisposableBean {

    public static ExecutorService poll = null;

    @PostConstruct
    public void init() {
        Executors.newFixedThreadPool(2);
    }

    @Override
    public void destroy() throws Exception {
        poll.shutdown();
    }
}
