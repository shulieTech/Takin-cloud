package io.shulie.takin.cloud.common.enums;

import java.util.HashMap;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * @author shiyajian
 * @date 2020-07-30
 */
@Getter
@AllArgsConstructor
public enum PressureModeEnum {

    /**
     * 固定模式
     */
    FIXED(1, "fixed"),

    /**
     * 线性增长
     */
    LINEAR(2, "linear"),

    /**
     * 阶梯增长
     */
    STAIR(3, "stair");

    /**
     * 编码
     */
    private final int code;
    /**
     * 名称
     */
    private final String description;

    public boolean equals(Integer code) {
        PressureModeEnum mode = PressureModeEnum.of(code);
        return this == mode;
    }

    private static final HashMap<Integer, PressureModeEnum> INSTANCES = new HashMap<>(3);

    static {
        for (PressureModeEnum e : PressureModeEnum.values()) {
            INSTANCES.put(e.getCode(), e);
        }
    }

    public static PressureModeEnum of(Integer code) {
        return INSTANCES.get(code);
    }

}
