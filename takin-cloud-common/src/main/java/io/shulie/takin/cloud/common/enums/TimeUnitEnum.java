package io.shulie.takin.cloud.common.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

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
    DAY(TimeUnit.DAYS, "d", "天"),
    /**
     * 时
     */
    HOUR(TimeUnit.HOURS, "h", "时"),
    /**
     * 分
     */
    MINUTE(TimeUnit.MINUTES, "m", "分"),
    /**
     * 秒
     */
    SECOND(TimeUnit.SECONDS, "s", "秒");

    private final TimeUnit unit;
    private final String value;
    private final String name;

    private static final Map<String, TimeUnitEnum> INSTANCES = new HashMap<>(4);

    static {
        for (TimeUnitEnum e : TimeUnitEnum.values()) {
            INSTANCES.put(e.value.toLowerCase(), e);
        }
    }

    public static TimeUnitEnum value(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        return INSTANCES.get(value.toLowerCase());
    }

}
