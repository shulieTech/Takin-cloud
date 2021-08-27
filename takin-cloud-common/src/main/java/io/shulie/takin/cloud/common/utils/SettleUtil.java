package io.shulie.takin.cloud.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @ClassName SettleUtil
 * @Description
 * @Author qianshui
 * @Date 2020/5/9 下午5:18
 */
public class SettleUtil {

    public static String direct(Integer direct) {
        return (direct == null || direct == 0) ? "+" : "-";
    }

    public static String format(BigDecimal balance) {
        if (balance == null) {
            return null;
        }
        return balance.setScale(2, RoundingMode.FLOOR).toString();
    }
}
