package io.shulie.takin.cloud.common.enums.engine;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 何仲奇
 * @date 2020/9/23 3:01 下午
 */
@Getter
@AllArgsConstructor
public enum EngineStatusEnum {
    /**
     * 启动成功
     */
    STARTED("启动成功", "started"),
    /**
     * 启动失败
     */
    START_FAILED("启动失败", "startFail"),
    /**
     * 中断
     */
    INTERRUPT("中断", "interrupt"),
    /**
     * 中断成功
     */
    INTERRUPT_SUCCEED("中断成功", "interruptSuccess"),
    /**
     * 中断失败
     */
    INTERRUPT_FAILED("中断失败", "interruptFail");

    private final String message;
    private final String status;

    private static final HashMap<String, EngineStatusEnum> INSTANCES = new HashMap<>(5);

    static {
        for (EngineStatusEnum e : EngineStatusEnum.values()) {
            INSTANCES.put(e.getStatus(), e);
        }
    }

    public static EngineStatusEnum of(String status) {
        return INSTANCES.get(status);
    }
}
