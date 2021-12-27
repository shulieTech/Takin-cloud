package io.shulie.takin.cloud.common.enums;

import java.util.HashMap;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 线程组类型
 *
 * @author liyuanba
 * @date 2021/11/1 2:02 下午
 */
@Getter
@AllArgsConstructor
public enum ThreadGroupTypeEnum {
    /**
     * 并发模式
     */
    CONCURRENCY(0, "并发模式"),
    /**
     * TPS模式
     */
    TPS(1, "TPS模式"),
    /**
     * 自定义模式
     */
    CUSTOMIZE(2, "自定义模式"),
    ;

    private final int code;
    private final String description;

    private static final HashMap<Integer, ThreadGroupTypeEnum> INSTANCES = new HashMap<>(3);

    static {
        for (ThreadGroupTypeEnum e : ThreadGroupTypeEnum.values()) {
            INSTANCES.put(e.getCode(), e);
        }
    }

    public static ThreadGroupTypeEnum of(Integer code) {
        if (null == code) {
            return null;
        }
        return INSTANCES.get(code);
    }

    public boolean equals(Integer code) {
        ThreadGroupTypeEnum type = of(code);
        return this == type;
    }
}
