package io.shulie.tro.schedule.taskmanage.Impl;

import com.github.pagehelper.PageInfo;
import io.shulie.takin.app.Application;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageQueryInput;
import io.shulie.takin.cloud.biz.output.scenemanage.SceneManageListOutput;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author mubai
 * @date 2020-12-03 20:05
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class SceneManageServiceTest {

    @Autowired
    private SceneManageService sceneManageService;

    @Test
    public void testQuery() {
        SceneManageQueryInput queryInput = new SceneManageQueryInput();
        // List<Long> list = Arrays.asList(113l, 114l, 115l, 116l, 117l, 118l);
        // queryInput.setSceneIds(list);

        queryInput.setLastPtStartTime("2020-12-03 09:11:00");
        queryInput.setLastPtEndTime("2020-12-03 20:08:00");
        PageInfo<SceneManageListOutput> sceneManageListOutputPageInfo =
            sceneManageService.queryPageList(queryInput);
        log.info("" + sceneManageListOutputPageInfo);
    }
}
