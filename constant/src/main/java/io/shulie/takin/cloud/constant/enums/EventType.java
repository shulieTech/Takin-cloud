package io.shulie.takin.cloud.constant.enums;

import java.util.Arrays;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Getter
@AllArgsConstructor
public enum EventType {
    /**
     * 消除警告
     */
    WATCHMAN_HEARTBEAT(100, "调度心跳上报"),
    WATCHMAN_UPLOAD(101, "调度资源上报"),
    RESOUECE_EXAMPLE_HEARTBEAT(200, "工作空间心跳上报"),
    RESOUECE_EXAMPLE_START(201, "工作空间启动上报"),
    RESOUECE_EXAMPLE_STOP(202, "工作空间停止上报"),
    COMMAND_ACK(300, "命令确认")
    // 格式化用
    ;
    @Getter
    private final Integer code;
    private final String description;
    private final static HashMap<Integer, EventType> EXAMPLE_MAP = new HashMap<>(6);

    static {
        Arrays.stream(values()).forEach(t -> EXAMPLE_MAP.put(t.getCode(), t));
    }

    public static EventType of(Integer code) {
        return EXAMPLE_MAP.get(code);
    }

}
