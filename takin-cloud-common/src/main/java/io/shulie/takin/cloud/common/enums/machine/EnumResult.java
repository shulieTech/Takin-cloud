package io.shulie.takin.cloud.common.enums.machine;

import lombok.Data;

/**
 * @author vernon
 * @date 2019/12/23 21:52
 */
@Data
public class EnumResult {
    /**
     * 用于逻辑的值
     */
    private String value = "";
    /**
     * '文案展示的值'
     */
    private String label = "";
    /**
     * 前端枚举序号
     */
    private Integer num = 0;
    /**
     * 前端不显示
     */
    private Boolean disable = false;

}
