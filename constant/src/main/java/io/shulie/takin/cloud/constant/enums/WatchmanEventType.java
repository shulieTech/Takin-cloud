package io.shulie.takin.cloud.constant.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 调度事件类型
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Getter
@AllArgsConstructor
public enum WatchmanEventType {
    /**
     * 心跳
     */
    HEARTBEAT(0, "心跳"),
    /**
     * 资源上报
     */
    RESOURCE(1, "资源上报"),
    // 格式化用
    ;

    private final Integer code;
    private final String description;
}
