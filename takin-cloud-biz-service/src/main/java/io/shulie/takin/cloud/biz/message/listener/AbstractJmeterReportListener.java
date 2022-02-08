package io.shulie.takin.cloud.biz.message.listener;

import io.shulie.jmeter.tool.redis.domain.GroupTopicEnum;

/**
 * @Author: liyuanba
 * @Date: 2022/1/28 2:35 下午
 */
public abstract class AbstractJmeterReportListener extends AbstractListener {
    private final GroupTopicEnum groupTopic = new GroupTopicEnum("default", "jmeter_report");

    @Override
    public GroupTopicEnum getGroupTopic() {
        return groupTopic;
    }
}
