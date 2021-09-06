package io.shulie.tro.schedule.taskmanage.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.shulie.takin.ext.content.enginecall.ScheduleRunRequest;
import io.shulie.takin.ext.content.enginecall.ScheduleStartRequestExt;
import io.shulie.takin.app.Application;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import io.shulie.takin.cloud.biz.service.schedule.ScheduleService;

/**
 * @author 莫问
 * @date 2020-05-12
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ScheduleTest {

    @Autowired
    private ScheduleService scheduleService;

    @Test
    public void testScchedule() {
        ScheduleRunRequest runRequest = new ScheduleRunRequest();
        long scheduleId = Integer.valueOf(RandomUtils.nextInt()).longValue();
        runRequest.setScheduleId(scheduleId);
        ScheduleStartRequestExt scheduleStartRequest = new ScheduleStartRequestExt();
        scheduleStartRequest.setContinuedTime(100L);
        scheduleStartRequest.setPressureMode("fixed");
        scheduleStartRequest.setTaskId(scheduleId + 1);
        //        scheduleStartRequest.setScriptPath("/etc/engine/script/66/test.jmx");
        scheduleStartRequest.setScriptPath("/etc/engine/script/99/file-split.jmx");
        scheduleStartRequest.setSceneId(scheduleId + 2);
        scheduleStartRequest.setTotalIp(2);
        scheduleStartRequest.setExpectThroughput(50);
        scheduleStartRequest.setRampUp(2L);
        scheduleStartRequest.setSteps(3);

        List<ScheduleStartRequestExt.DataFile> list = new ArrayList<>();

        ScheduleStartRequestExt.DataFile dataFile = new ScheduleStartRequestExt.DataFile();
        dataFile.setName("abc.csv");
        dataFile.setPath("/etc/engine/script/99/abc.csv");
        dataFile.setSplit(true);

        ScheduleStartRequestExt.DataFile dataFile1 = new ScheduleStartRequestExt.DataFile();
        dataFile1.setName("aaaa.csv");
        dataFile1.setPath("/etc/engine/script/99/aaaa.csv");
        dataFile1.setSplit(true);
        //
        ScheduleStartRequestExt.DataFile dataFile2 = new ScheduleStartRequestExt.DataFile();
        dataFile2.setName("bbbb.csv");
        dataFile2.setPath("/etc/engine/script/99/bbbb.csv");
        dataFile2.setSplit(false);
        list.add(dataFile);
        list.add(dataFile1);
        list.add(dataFile2);

        scheduleStartRequest.setDataFile(list);
        Map<String, String> map = new HashMap<>();
        map.put("abc", "123456");
        scheduleStartRequest.setBusinessData(map);
        runRequest.setRequest(scheduleStartRequest);

        scheduleService.startSchedule(scheduleStartRequest);

    }

}
