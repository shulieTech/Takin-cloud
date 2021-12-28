package io.shulie.takin.cloud.common.enums.deployment;

import java.util.HashMap;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * @author 无涯
 * @date 2021/4/25 1:58 下午
 */
@Getter
@AllArgsConstructor
public enum DeploymentMethodEnum {
    /**
     * 私有化
     */
    PRIVATE(0, "private"),
    /**
     * 公开的
     */
    PUBLIC(1, "public");
    public final Integer code;
    private final String description;

    private static final HashMap<Integer, DeploymentMethodEnum> CODE_INSTANCES = new HashMap<>(2);
    private static final HashMap<String, DeploymentMethodEnum> DESCRIPTION_INSTANCES = new HashMap<>(2);

    static {
        for (DeploymentMethodEnum e : DeploymentMethodEnum.values()) {
            CODE_INSTANCES.put(e.getCode(), e);
            DESCRIPTION_INSTANCES.put(e.getDescription(), e);
        }
    }

    public static DeploymentMethodEnum of(String desc) {
        return DESCRIPTION_INSTANCES.getOrDefault(desc, DeploymentMethodEnum.PRIVATE);
    }

    public static DeploymentMethodEnum of(Integer code) {
        return CODE_INSTANCES.getOrDefault(code, DeploymentMethodEnum.PRIVATE);
    }

}
