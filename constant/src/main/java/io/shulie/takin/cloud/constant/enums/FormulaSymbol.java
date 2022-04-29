package io.shulie.takin.cloud.constant.enums;

import java.util.Arrays;
import java.util.HashMap;

import lombok.Getter;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 算数符号
 * <p>(>=、>、=、<=、<)</p>
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Getter
@AllArgsConstructor
public enum FormulaSymbol {
    /**
     * 消除警告
     */
    GREATER_THAN_OR_EQUAL_TO(20, ">=", "大于等于"),
    GREATER_THAN(21, ">", "大于"),
    EQUAL(0, "=", "等于"),
    LESS_THAN_OR_EQUAL_TO(10, "<=", "小于等于"),
    LESS_THAN(11, ">", "小于"),
    // 格式化用
    ;
    @Getter
    @JsonValue
    private final Integer code;
    private final String symbol;
    private final String description;

    @Override
    public String toString() {return code + ":" + description;}

    private final static HashMap<Integer, FormulaSymbol> EXAMPLE_MAP = new HashMap<>(6);

    static {
        Arrays.stream(values()).forEach(t -> EXAMPLE_MAP.put(t.getCode(), t));
    }

    public static FormulaSymbol of(Integer code) {
        return EXAMPLE_MAP.get(code);
    }

}
