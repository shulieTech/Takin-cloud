package io.shulie.takin.cloud.biz.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liyuanba
 * @Date: 2022/2/9 11:15 上午
 */
public enum PressureTaskStatusEnum {
    /**
     * 启动中
     */
    STARTING(0),
    /**
     * 压测中
     */
    TESTING(1),
    /**
     * 停止压测
     */
    STOPED(2),
    /**
     * 压测因为异常而结束
     */
    FAILED(3),
    ;
    @Getter
    private final int code;

    PressureTaskStatusEnum(int code) {
        this.code = code;
    }

    private static Map<Integer, PressureTaskStatusEnum> pool = new HashMap<>();

    static {
        for (PressureTaskStatusEnum e : PressureTaskStatusEnum.values()) {
            pool.put(e.getCode(), e);
        }
    }

    public static PressureTaskStatusEnum value(Integer code) {
        if (null == code) {
            return null;
        }
        return pool.get(code);
    }
}
