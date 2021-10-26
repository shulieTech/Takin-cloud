/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.plugin.engine.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Author: liyuanba
 * @Date: 2021/9/24 4:31 下午
 */
public class NumberUtil {
    public static int parseInt(String s) {
        return parseInt(s, 0);
    }

    public static Integer parseInt(String s, Integer defValue) {
        if (StringUtils.isBlank(s) || !StringUtils.isNumeric(s)) {
            return defValue;
        }
        if (null != defValue) {
            return NumberUtils.toInt(s, defValue);
        } else {
            int a = NumberUtils.toInt(s, Integer.MIN_VALUE);
            if (a == Integer.MIN_VALUE) {
                return defValue;
            }
            return a;
        }
    }

    /**
     * 计算a在b中的比例
     */
    public static double getRate(Number a, Number b) {
        return getRate(a, b, 0d);
    }

    public static double getRate(Number a, Number b, double defValue) {
        return getRate(a, b, false, defValue, 2, RoundingMode.HALF_UP);
    }

    /**
     * 计算a在b中的百分比
     */
    public static double getPercentRate(Number a, Number b) {
        return getPercentRate(a, b, 0d);
    }

    public static double getPercentRate(Number a, Number b, double defValue) {
        return getRate(a, b, true, defValue, 2, RoundingMode.HALF_UP);
    }

    /**
     * 计算a在b中的比例
     * @param a         被除数
     * @param b         除数
     * @param percent   是否是百分比
     * @param defValue  默认值
     * @param scale     小数点后精度, 默认值10
     * @param roundMode 取整模式，默认四舍五入
     * @return 返回a除以b的值
     */
    public static Double getRate(Number a, Number b, boolean percent, Double defValue, Integer scale, RoundingMode roundMode) {
        if (null == b) {
            return defValue;
        }
        if (null == a) {
            return defValue;
        }
        BigDecimal aa = BigDecimal.valueOf(a.doubleValue());
        BigDecimal bb = BigDecimal.valueOf(b.doubleValue());
        if (percent) {
            aa = aa.multiply(new BigDecimal(100));
        }
        if (null == scale) {
            scale = 10;
        }
        if (null == roundMode) {
            roundMode = RoundingMode.HALF_UP;
        }
        return aa.divide(bb, scale, roundMode).doubleValue();
    }
}
