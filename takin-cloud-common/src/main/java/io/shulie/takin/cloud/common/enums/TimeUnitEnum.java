package io.shulie.takin.cloud.common.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * @author qianshui
 * @date 2020/5/11 下午7:58
 */
@Getter
@AllArgsConstructor
public enum TimeUnitEnum {
    /**
     * 天
     */
    DAY("d", "天"),
    /**
     * 时
     */
    HOUR("h", "时"),
    /**
     * 分
     */
    MINUTE("m", "分"),
    /**
     * 秒
     */
    SECOND("s", "秒");

    private String value;
    private String name;
}
