package io.shulie.takin.cloud.common.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaoyong
 */
public enum PressureTypeEnums {

    /**
     * 并发模式
     */
    CONCURRENCY("concurrency", 0),

    /**
     * tps目标模式
     */
    TPS("linear", 1),

    /**
     * 自定义模式
     */
    PERSONALIZATION("personalization", 2),

    /**
     * 流量调试模式
     */
    FLOW_DEBUG("flow_debug",3),

    /**
     * 巡检模式
     */
    INSPECTION_MODE("inspection_mode",4),


    /**
     * 试跑模式
     */
    TRY_RUN("try_run", 5)
    ;

    /**
     * 名称
     */
    @Getter
    private final String text;

    /**
     * 编码
     */
    @Getter
    private final int code;

    private static final Map<Integer, PressureTypeEnums> pool = new HashMap<>();
    static {
        for (PressureTypeEnums e : PressureTypeEnums.values()) {
            pool.put(e.getCode(), e);
        }
    }

    public static PressureTypeEnums value(Integer code) {
        if (null == code) {
            return PressureTypeEnums.CONCURRENCY;
        }
        return pool.get(code);
    }

    PressureTypeEnums(String text, int code) {
        this.text = text;
        this.code = code;
    }

    /**
     * PressureTypeEnums.equels(code)
     */
    public boolean equels(Integer code) {
        PressureTypeEnums pressureType = value(code);
        return this == pressureType;
    }


    public static boolean isConcurrency(Integer code){
        //为空默认为并发模式
        return code == null || PressureTypeEnums.CONCURRENCY.getCode() == code;
    }

    public static boolean isTps(Integer code){
        return code != null && PressureTypeEnums.TPS.getCode() == code;
    }

    public static boolean isPersonalization(Integer code){
        return code != null && PressureTypeEnums.PERSONALIZATION.getCode() == code;
    }

}
