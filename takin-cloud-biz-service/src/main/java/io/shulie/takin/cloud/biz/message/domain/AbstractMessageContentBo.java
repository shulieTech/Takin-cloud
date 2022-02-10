package io.shulie.takin.cloud.biz.message.domain;

import io.shulie.jmeter.tool.redis.domain.AbstractEntry;

/**
 * @Author: liyuanba
 * @Date: 2022/2/10 4:12 下午
 */
public abstract class AbstractMessageContentBo extends AbstractEntry {
    /**
     * 消息的key
     */
    public abstract String getKey();
}
