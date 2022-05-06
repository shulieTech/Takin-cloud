package io.shulie.takin.cloud.constant.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonValue;

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
    JOB_EXAMPLE_HEARTBEAT(200, "任务实例心跳"),
    JOB_EXAMPLE_START(201, "任务实例启动"),
    JOB_EXAMPLE_STOP(202, "任务实例停止"),
    JOB_EXAMPLE_ERROR(203, "任务实例异常"),
    SLA(301, "触发SLA"),
    // 格式化用
    ;
    @JsonValue
    private final Integer code;
    private final String description;
}
