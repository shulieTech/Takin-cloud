package io.shulie.tro.sys;

import io.shulie.takin.app.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class InfoTest {

    @Value("${info.app.version}")
    private String version;
    @Test
    public void test(){
        System.out.println(version);
    }
}
