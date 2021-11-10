package io.shulie.takin.cloud.common.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaoyong
 */
public enum PressureSceneEnum {
    DEFAULT(0, "常规模式"),

    FLOW_DEBUG(3,"流量调试"),

    INSPECTION_MODE(4,"巡检模式"),

    TRY_RUN(5, "试跑模式")
    ;

    @Getter
    private int code;

    @Getter
    private String description;

    private static final Map<Integer, PressureSceneEnum> instances = new HashMap<>();

    static {
        for(PressureSceneEnum e : PressureSceneEnum.values()) {
            instances.put(e.getCode(), e);
        }
        //为了兼容老版本数据，将1，2转化为常规模式
        instances.put(1, DEFAULT);
        instances.put(2, DEFAULT);
    }

    public static PressureSceneEnum value(Integer code) {
        if (null == code) {
            return null;
        }
        return instances.get(code);
    }
    public static String toDoc() {
        StringBuilder sb = new StringBuilder();
        for(PressureSceneEnum e : PressureSceneEnum.values()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(e.getCode()).append(e.getDescription());
        }
        return sb.toString();
    }

    PressureSceneEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * PressureTypeEnums.equels(code)
     */
    public boolean equals(Integer code) {
        PressureSceneEnum pressureType = value(code);
        return this == pressureType;
    }
}
