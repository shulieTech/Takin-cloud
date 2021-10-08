package io.shulie.takin.cloud.service;

import io.shulie.takin.app.Application;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.data.mapper.mysql.MiddlewareJarMapper;
import io.shulie.takin.cloud.data.param.middleware.CreateMiddleWareJarParam;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import javax.annotation.Resource;

/**
 * @author liuchuan
 * @date 2021/5/17 5:09 下午
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class MiddlewareJarServiceTest {
    @Resource(type = RedisClientUtils.class)
    private RedisClientUtils redisClientUtils;
    @Resource(type = MiddlewareJarMapper.class)
    private MiddlewareJarMapper middlewareJarMapper;

    @Test
    public void testRedis() {
        //Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(
        //    String.format(TryRunConstants.UPLOAD_TASK_STATUS + "_%d_%d_%s", 2L, 1L, "test"), 1L,
        //    TryRunConstants.DEFAULT_EXPIRE_TIME,
        //    TimeUnit.MINUTES);
        //log.info(ifAbsent);
    }

    @Test
    public void testSetString() {
        redisClientUtils.setString("a", "b");
        redisClientUtils.delete("a");
    }

    @Test
    public void testByDeleteByAvgListBatch() {
        log.info("test" + middlewareJarMapper.deleteByAgvList(
            Arrays.asList("druid_com.alibaba_1.0.6", "druid_com.alibaba_1.0.7", "druid_com.alibaba_")));
    }

    @Test
    public void testInsertBatch() {
        CreateMiddleWareJarParam param = new CreateMiddleWareJarParam();
        param.setStatus(1);
        param.setType("连接池");
        param.setName("druid");
        param.setGroupId("com.alibaba");
        param.setArtifactId("druid");
        param.setVersion("1.0.6");

        CreateMiddleWareJarParam param2 = new CreateMiddleWareJarParam();
        param2.setStatus(1);
        param2.setType("连接池");
        param2.setName("druid");
        param2.setGroupId("com.alibaba");
        param2.setArtifactId("druid");
        param2.setVersion("1.0.7");
        log.info("test" + middlewareJarMapper.insertBatch(Arrays.asList(param, param2)));
    }

}
