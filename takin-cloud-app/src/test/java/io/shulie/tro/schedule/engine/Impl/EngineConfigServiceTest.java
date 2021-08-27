package io.shulie.takin.schedule.engine.Impl;

import io.shulie.takin.app.Application;
import io.shulie.takin.cloud.biz.service.engine.EngineConfigService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class EngineConfigServiceTest {

    @Autowired
    EngineConfigService engineConfigService;

//    @Test
//    public void testGetServerAndPort(){
//        String logPushServer = engineConfigService.getLogPushServer("192.168.1.211:29900");
//        System.out.println(logPushServer);
//    }
//
//    @Test
//    public void testGetLogSimpling(){
//        String logSimpling = engineConfigService.getLogSimpling();
//        System.out.println(logSimpling);
//    }
}
