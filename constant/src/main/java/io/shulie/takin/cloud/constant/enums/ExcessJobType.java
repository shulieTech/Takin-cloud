package io.shulie.takin.cloud.constant.enums;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

import lombok.Getter;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 定时任务类型
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Getter
@AllArgsConstructor
public enum ExcessJobType {
    /**
     * 消除警告
     */
    DATA_CALIBRATION(0, "数据校准"),
    IGNORE(Integer.MAX_VALUE, "忽略项"),
    // 格式化用
    ;
    @JsonValue
    private final Integer code;
    private final String description;

    private static final Map<Integer, ExcessJobType> EXAMPLE_MAP = new HashMap<>(8);

    static {
        Arrays.stream(values()).forEach(t -> EXAMPLE_MAP.put(t.getCode(), t));
    }

    public static ExcessJobType of(Integer code) {
        return EXAMPLE_MAP.get(code);
    }
}
