package io.shulie.takin.cloud.biz.message;

import io.shulie.jmeter.tool.redis.RedisConfig;
import io.shulie.jmeter.tool.redis.domain.GroupTopicEnum;
import io.shulie.jmeter.tool.redis.domain.TkMessage;
import io.shulie.jmeter.tool.redis.message.MessageProducer;
import io.shulie.takin.cloud.biz.message.domain.AbstractMessageContentBo;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @Author: liyuanba
 * @Date: 2022/1/28 2:15 下午
 */
@Slf4j
@Service
public class MessageProducerService {
    /**
     * 通知压测引擎
     */
    public static final GroupTopicEnum GT_NOTIFY_ENGINE = new GroupTopicEnum("default", "notify_engine");

    @Resource
    private RedisConfig redisConfig;
    private MessageProducer messageProducer;
    @PostConstruct
    private void init() {
        messageProducer = MessageProducer.getInstance(redisConfig);
    }

    public boolean send(String tag, AbstractMessageContentBo content) {
        TkMessage message = TkMessage.create()
                .setGroupTopic(GT_NOTIFY_ENGINE)
                .setTag(tag)
                .setKey(content.getKey())
                .setContent(JsonUtil.toJson(content))
                .build();
        return send(message);
    }

    public boolean send(TkMessage message) {
        try {
            return messageProducer.send(message);
        } catch (Exception e) {
            log.error("send message failed!message="+ JsonUtil.toJson(message), e);
        }
        return false;
    }
}
