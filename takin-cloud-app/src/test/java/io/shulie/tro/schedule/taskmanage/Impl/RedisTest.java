package io.shulie.takin.schedule.taskmanage.Impl;

import io.shulie.takin.app.Application;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName RedisTest
 * @Description
 * @Author qianshui
 * @Date 2020/11/20 下午12:26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class RedisTest {

    @Autowired
    private RedisClientUtils redisClientUtils;

    @Test
    public void testRedis() {
        System.out.println(redisClientUtils.zsetAdd("set1", "1"));
        System.out.println(redisClientUtils.zsetAdd("set1", "2"));
        System.out.println(redisClientUtils.zsetAdd("set1", "1"));
        System.out.println(redisClientUtils.zsetAdd("set1", "3"));
    }
}
