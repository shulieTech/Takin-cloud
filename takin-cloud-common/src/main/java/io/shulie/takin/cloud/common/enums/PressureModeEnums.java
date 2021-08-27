package io.shulie.takin.cloud.common.enums;

import lombok.Getter;

/**
 * @author shiyajian
 * create: 2020-07-30
 */
public enum PressureModeEnums {

    /**
     * 固定模式
     */
    FIXED("fixed", 1),

    /**
     * 线性增长
     */
    LINEAR("linear", 2),

    /**
     * 阶梯增长
     */
    STAIR("stair", 3);

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

    PressureModeEnums(String text, int code) {
        this.text = text;
        this.code = code;
    }
}
