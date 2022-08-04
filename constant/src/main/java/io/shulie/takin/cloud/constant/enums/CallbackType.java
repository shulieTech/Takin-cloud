package io.shulie.takin.cloud.constant.enums;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

import lombok.Getter;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

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
    // 资源实例相关
    RESOURCE_EXAMPLE_HEARTBEAT(100, "资源实例(Pod)心跳"),
    RESOURCE_EXAMPLE_START(101, "资源实例启动"),
    RESOURCE_EXAMPLE_STOP(102, "资源实例停止"),
    RESOURCE_EXAMPLE_ERROR(103, "资源实例异常"),
    // 任务实例相关
    JOB_EXAMPLE_HEARTBEAT(200, "任务实例心跳"),
    JOB_EXAMPLE_START(201, "任务实例启动"),
    JOB_EXAMPLE_STOP(202, "任务实例停止"),
    JOB_EXAMPLE_ERROR(203, "任务实例异常"),
    JOB_EXAMPLE_SUCCESSFUL(204, "任务实例正常停止"),
    // 过程数据[sla/csv用量]
    SLA(301, "触发SLA"),
    EXCESS_JOB(302, "额外的任务"),
    FILE_USAGE(303, "文件用量"),
    // 文件资源
    FILE_RESOURCE_FAIL(400, "文件资源失败"),
    FILE_RESOURCE_COMPLETE(401, "文件资源完成"),
    FILE_RESOURCE_PROGRESS(402, "文件资源进度"),
    // 格式化用
    ;
    @JsonValue
    private final Integer code;
    private final String description;

    private static final Map<Integer, CallbackType> EXAMPLE_MAP = new HashMap<>(8);

    static {
        Arrays.stream(values()).forEach(t -> EXAMPLE_MAP.put(t.getCode(), t));
    }

    @JsonCreator
    public static CallbackType of(Integer code) {
        return EXAMPLE_MAP.get(code);
    }
}
