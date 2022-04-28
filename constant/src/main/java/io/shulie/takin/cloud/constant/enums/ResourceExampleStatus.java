package io.shulie.takin.cloud.constant.enums;

import java.util.Arrays;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务实例状态
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Getter
@AllArgsConstructor
public enum ResourceExampleStatus {
    /**
     * 消除警告
     */
    PENDING(0, "待启动"),
    STARTED(1, "已启动"),
    STOPED(2, "已停止"),
    ABNORMAL(3, "发生异常"),
    ;
    @JsonValue
    private final Integer code;
    private final String description;

    private final static HashMap<Integer, ResourceExampleStatus> EXAMPLE_MAP = new HashMap<>(6);

    static {
        Arrays.stream(values()).forEach(t -> EXAMPLE_MAP.put(t.getCode(), t));
    }

    public static ResourceExampleStatus of(Integer code) {
        return EXAMPLE_MAP.get(code);
    }

    @Override
    public String toString() {return code + ":" + description;}
}
