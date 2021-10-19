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

package io.shulie.takin.cloud.common.utils;

import java.math.BigDecimal;

/**
 * @Author: liyuanba
 * @Date: 2021/9/24 4:31 下午
 */
public class NumberUtil {
    /**
     * 计算a在b中的比例
     */
    public static double getRate(Number a, Number b) {
        return getRate(a, b, 0d);
    }

    public static double getRate(Number a, Number b, double defValue) {
        return getRate(a, b, false, defValue, 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 计算a在b中的百分比
     */
    public static double getPercentRate(Number a, Number b) {
        return getPercentRate(a, b, 0d);
    }

    public static double getPercentRate(Number a, Number b, double defValue) {
        return getRate(a, b, true, defValue, 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 计算a在b中的比例
     * @param a         被除数
     * @param b         除数
     * @param percent   是否是百分比
     * @param defValue  默认值
     * @param scale     小数点后精度, null表示不做精度取舍
     * @param roundMode 取整模式，默认四舍五入
     * @return
     */
    public static Double getRate(Number a, Number b, boolean percent, Double defValue, Integer scale, Integer roundMode) {
        if (null == b) {
            return defValue;
        }
        if (null == a) {
            return defValue;
        }
        BigDecimal aa = new BigDecimal(a.doubleValue());
        BigDecimal bb = new BigDecimal(b.doubleValue());
        if (percent) {
            aa = aa.multiply(new BigDecimal(100));
        }
        if (null == scale) {
            return aa.divide(bb).doubleValue();
        }
        if (null == roundMode) {
            roundMode = BigDecimal.ROUND_HALF_UP;
        }
        return aa.divide(bb, scale, roundMode).doubleValue();
    }
}
