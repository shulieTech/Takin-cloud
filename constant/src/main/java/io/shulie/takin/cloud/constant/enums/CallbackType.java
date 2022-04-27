package io.shulie.takin.cloud.constant.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 回调类型
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Getter
@AllArgsConstructor
public enum CallbackType {
    /**
     * 消除警告
     */
    RESOURCE_EXAMPLE_HEARTBEAT(100, "资源实例(Pod)心跳"),
    RESOURCE_EXAMPLE_START(101, "资源实例启动"),
    RESOURCE_EXAMPLE_STOP(102, "资源实例停止"),
    RESOURCE_EXAMPLE_ERROR(103, "资源实例异常"),
    JMETER_HEARTBEAT(200, "JMeter心跳"),
    JMETER_START(201, "JMeter启动"),
    JMETER_STOP(202, "JMeter停止"),
    JMETER_ERROR(203, "JMeter异常"),
    // 格式化用
    ;

    private final Integer code;
    private final String description;
}
