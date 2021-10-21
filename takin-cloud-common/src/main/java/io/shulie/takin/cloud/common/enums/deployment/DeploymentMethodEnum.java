package io.shulie.takin.cloud.common.enums.deployment;

import java.util.Map;
import java.util.HashMap;

import cn.hutool.core.util.StrUtil;
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
    public Integer type;
    private String desc;

    private static Map<String, DeploymentMethodEnum> pool = new HashMap<>();

    static {
        for (DeploymentMethodEnum e : DeploymentMethodEnum.values()) {
            pool.put(e.getDesc(), e);
        }
    }

    public static DeploymentMethodEnum valueBy(String desc) {
        if (StrUtil.isBlank(desc)) {
            return null;
        }
        return pool.get(desc);
    }

    public static String getByType(Integer type) {
        if (type == null) {
            return DeploymentMethodEnum.PUBLIC.getDesc();
        }
        for (DeploymentMethodEnum methodEnum : DeploymentMethodEnum.values()) {
            if (methodEnum.type.equals(type)) {
                return methodEnum.getDesc();
            }
        }
        // 默认私有化
        return DeploymentMethodEnum.PRIVATE.getDesc();
    }

    public boolean equals(String desc) {
        if (StrUtil.isBlank(desc)) {
            return false;
        }
        DeploymentMethodEnum input = DeploymentMethodEnum.valueBy(desc);
        return this == input;
    }
}
