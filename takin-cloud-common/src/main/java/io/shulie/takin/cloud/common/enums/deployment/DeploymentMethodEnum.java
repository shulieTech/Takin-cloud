package io.shulie.takin.cloud.common.enums.deployment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 无涯
 * @Package io.shulie.takin.cloud.common.enums.deployment
 * @date 2021/4/25 1:58 下午
 */
@AllArgsConstructor
@Getter
public enum DeploymentMethodEnum {
    PRIVATE(0,"private"),
    PUBLIC( 1,"public");
    public Integer type;
    private String desc;

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

}
