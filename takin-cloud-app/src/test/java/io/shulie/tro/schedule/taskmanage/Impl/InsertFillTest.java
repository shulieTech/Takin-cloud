package io.shulie.tro.schedule.taskmanage.Impl;

import io.shulie.takin.app.Application;
import io.shulie.takin.cloud.data.mapper.mysql.ReportMapper;
import io.shulie.takin.cloud.data.mapper.mysql.SceneManageMapper;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author qianshui
 * @date 2020/10/27 下午5:32
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class InsertFillTest {

    @Autowired
    SceneManageMapper sceneManageMapper;

    @Autowired
    ReportMapper reportMapper;

    @Test
    public void insertSceneManage() {
        SceneManageEntity entity = new SceneManageEntity();
        entity.setSceneName("Test_scene_name");
        entity.setStatus(0);
        entity.setIsDeleted(1);
        sceneManageMapper.insert(entity);
    }

    @Test
    public void insertReport() {
        ReportEntity entity = new ReportEntity();
        entity.setSceneId(1L);
        entity.setSceneName("Test_scene_name");
        entity.setIsDeleted(1);
        reportMapper.insert(entity);
    }
}
