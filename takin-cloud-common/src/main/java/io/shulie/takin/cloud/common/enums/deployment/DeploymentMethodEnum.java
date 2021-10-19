package io.shulie.takin.cloud.common.enums.deployment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 无涯
 * @date 2021/4/25 1:58 下午
 */
@AllArgsConstructor
@Getter
public enum DeploymentMethodEnum {
    PRIVATE(0,"private"),
    PUBLIC( 1,"public");
    public Integer type;
    private String desc;

    private static Map<String, DeploymentMethodEnum> pool = new HashMap<>();
    static {
        for (DeploymentMethodEnum e : DeploymentMethodEnum.values()) {
            pool.put(e.getDesc(), e);
        }
    }

    public static DeploymentMethodEnum valueBy(String desc) {
        if (StringUtils.isBlank(desc)) {
            return null;
        }
        return pool.get(desc);
    }

    public static String getByType(Integer type) {
        if(type == null) {
            return DeploymentMethodEnum.PUBLIC.getDesc();
        }
        for(DeploymentMethodEnum methodEnum :DeploymentMethodEnum.values()) {
            if(methodEnum.type.equals(type)) {
                return methodEnum.getDesc();
            }
        }
        // 默认私有化
        return DeploymentMethodEnum.PRIVATE.getDesc();
    }

    public boolean equals(String desc) {
        if (StringUtils.isBlank(desc)) {
            return false;
        }
        DeploymentMethodEnum input = DeploymentMethodEnum.valueBy(desc);
        return this == input;
    }
}
