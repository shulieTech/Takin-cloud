package io.shulie.takin.plugins.enginecall;


import io.shulie.takin.app.Application;
import io.shulie.takin.cloud.common.utils.EnginePluginUtils;
import io.shulie.takin.ext.api.EngineCallExtApi;
import io.shulie.takin.ext.content.enginecall.ScheduleRunRequest;
import io.shulie.takin.ext.content.enginecall.ScheduleStartRequestExt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class EngineCallServiceTest {
    @Autowired
    private EnginePluginUtils enginePluginUtils;

    @Test
    public void createJob() {
        Long sceneId = 1L;
        Long taskId = 1L;
        Long customerId = -1L;
        ScheduleRunRequest request = new ScheduleRunRequest();
        request.setScheduleId(1L);
        ScheduleStartRequestExt requestExt = new ScheduleStartRequestExt();
        //jmeter
        requestExt.setEngineType("0");
        //并发模式
        requestExt.setPressureScene(0);
        requestExt.setScriptPath("/Users/shulie/Downloads/zhaoyong-test-01.jmx");
        requestExt.setTotalIp(1);
//        requestExt.setFileContinueRead();
//        requestExt.setDataFile();
//        requestExt.setContinuedTime();
        //固定压力值
//        requestExt.setPressureType(1);
        //并发
        requestExt.setExpectThroughput(2);
//        requestExt.setRampUp();
//        requestExt.setSteps(0);
        requestExt.setConsole("http://192.168.1.71:10010/tro-cloud/api/collector/receive");
//        requestExt.setTps();
        Map<String, String> businessData = new HashMap<>();
        businessData.put("HTTP请求", "100");
        requestExt.setBusinessData(businessData);
//        requestExt.setBusinessTpsData();

//        requestExt.setLoopsNum();
//        requestExt.setFixedTimer();
        requestExt.setInspect(false);
        requestExt.setTryRun(false);
        requestExt.setSceneId(sceneId);
        requestExt.setTaskId(taskId);
        requestExt.setCustomerId(customerId);
//        requestExt.setExtend();

        request.setRequest(requestExt);
//        EngineCallExtApi engineCallExtApi = enginePluginUtils.getEngineCallExtApi();
//        engineCallExtApi.buildJob(request);
    }

}
