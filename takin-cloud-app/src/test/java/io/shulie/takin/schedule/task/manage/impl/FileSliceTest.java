package io.shulie.takin.schedule.task.manage.impl;

import java.util.ArrayList;
import java.util.Map;

import io.shulie.takin.app.Application;
import io.shulie.takin.cloud.biz.service.schedule.FileSliceService;
import io.shulie.takin.cloud.common.utils.FileSliceByLine;
import io.shulie.takin.cloud.common.utils.FileSliceByLine.FileSliceInfo;
import io.shulie.takin.cloud.common.utils.FileSliceByPodNum;
import io.shulie.takin.cloud.common.utils.FileSliceByPodNum.Builder;
import io.shulie.takin.cloud.common.utils.FileSliceByPodNum.StartEndPair;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneBigFileSliceDAO;
import io.shulie.takin.cloud.data.model.mysql.SceneScriptRefEntity;
import io.shulie.takin.cloud.data.param.scenemanage.SceneBigFileSliceParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = Application.class)
public class FileSliceTest {

    @Autowired
    FileSliceService fileSliceService;

    @Autowired
    SceneBigFileSliceDAO fileSliceDAO;

    @Test
    public void testGetRef() {
        SceneScriptRefEntity entity = fileSliceDAO.selectRef(new SceneBigFileSliceParam() {{
            setFileName("OrderInfo_02.csv");
            setSceneId(180L);
        }});
        System.out.println(entity);
    }

    @Test
    public void sliceFileWithOrder() {
        FileSliceByLine fileSliceUtil =
            new FileSliceByLine.Builder("/Users/moriarty/Desktop/test/data.csv")
                .withSeparator(",")
                .withOrderColumnNum(null)
                .build();
        Map<Integer, FileSliceInfo> resultMap = fileSliceUtil.sliceFile();

        for (Map.Entry<Integer, FileSliceInfo> entry : resultMap.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue().toString());
        }
    }

    @Test
    public void sliceFileWithoutOrder() {
        FileSliceByPodNum build = new Builder("/Users/moriarty/Desktop/OrderInfo_01.csv")
            .withPartSize(3)
            .build();
        ArrayList<StartEndPair> startEndPairs = build.getStartEndPairs();
        startEndPairs.forEach(System.out::println);
    }
}
