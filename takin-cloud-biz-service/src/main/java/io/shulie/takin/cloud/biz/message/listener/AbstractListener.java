package io.shulie.takin.cloud.biz.message.listener;

import io.shulie.jmeter.tool.redis.RedisConfig;
import io.shulie.jmeter.tool.redis.message.AbstractMessageConsumerListener;
import io.shulie.jmeter.tool.redis.message.MessageConsumer;
import io.shulie.takin.cloud.biz.utils.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @Author: liyuanba
 * @Date: 2022/1/28 2:32 下午
 */
public abstract class AbstractListener extends AbstractMessageConsumerListener {
    @Resource
    private RedisConfig redisConfig;
    private MessageConsumer messageConsumer;

    @PostConstruct
    private void init() {
        messageConsumer = MessageConsumer.getInstance(redisConfig);
        messageConsumer.setThreadPool(Executors.getThreadPool());
        messageConsumer.register(this);
    }

}
