package io.shulie.takin.schedule.taskmanage.Impl;

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
    public void testFileSlice() {
        //boolean fileNeedSlice = fileSliceDAO.isFileNeedSlice(746L);
        //System.out.println(fileNeedSlice);
        //boolean b = fileSliceService.fileSlice(new FileSliceRequest() {{
        //    setFileName("data01.csv");
        //    setFilePath("Desktop/filetest/data01.csv");
        //    setSceneId(200L);
        //    setRefId(974L);
        //    setSplit(true);
        //    setPodNum(10);
        //    setOrderSplit(false);
        //    //setOrderColumnNum(2);
        //}});
        //    SceneBigFileSliceEntity sliceEntity = fileSliceDAO.selectOne(new SceneBigFileSliceParam() {{
        //        setFileRefId(974L);
        //        setSceneId(200L);
        //        setFileName("OrderInfo_02.csv");
        //    }});
        //
        //Map<Integer, JSONObject> sliceInfo = JSONObject.parseObject(sliceEntity.getSliceInfo(),
        //    Map.class);
        //List<StartEndPair> pairs = new ArrayList<>();
        //StartEndPair pair;
        //for (Map.Entry<Integer, JSONObject> entry : sliceInfo.entrySet()) {
        //    pair = new StartEndPair();
        //    pair.setStart(entry.getValue().getLong("start"));
        //    pair.setEnd(entry.getValue().getLong("end"));
        //    pair.setPartition(entry.getKey().toString());
        //    pairs.add(pair);
        //}
        //pairs.forEach(System.out::println);
    }

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
