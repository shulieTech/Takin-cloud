package io.shulie.takin.cloud.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 施压类型
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@AllArgsConstructor
public enum PressureType {
    /**
     * 常规模式
     */
    INITIAL(0, "常规模式"),
    /**
     * 调试模式
     */
    DEBUG(3, "调试模式"),
    /**
     * 巡检模式
     */
    PATROL(4, "巡检模式"),
    /**
     * 试跑模式
     */
    TRY(5, "试跑模式"),
    // 格式化用
    ;

    @Getter
    @JsonValue
    private final Integer code;
    @Getter
    private final String description;

    private static final Map<Integer, PressureType> CODE_MAPPING = new HashMap<>(8);

    static {
        Arrays.stream(values()).forEach(t -> CODE_MAPPING.put(t.getCode(), t));
    }

    @JsonCreator
    public static PressureType of(Integer code) {
        return CODE_MAPPING.get(code);
    }

    @Override
    public String toString() {return code + ":" + description;}
}
