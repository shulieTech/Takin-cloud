package io.shulie.takin.cloud.service;

import io.shulie.takin.app.Application;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageQueryInput;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.data.mapper.mysql.MiddlewareJarMapper;
import io.shulie.takin.cloud.data.param.middleware.CreateMiddleWareJarParam;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author liuchuan
 * @date 2021/5/17 5:09 下午
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("dev")
public class SceneManageServiceTest {
    @Autowired
    private SceneManageService sceneManageService;

    @Test
    public void testPageList() {
        SceneManageQueryInput queryVO = new SceneManageQueryInput();
        sceneManageService.queryPageList(queryVO);
    }

}
