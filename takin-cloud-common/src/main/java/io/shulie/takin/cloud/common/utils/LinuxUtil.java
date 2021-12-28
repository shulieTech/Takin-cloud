package io.shulie.takin.cloud.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;

import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @author qianshui
 * @date 2020/4/18 下午4:14
 */
@Slf4j
public class LinuxUtil {

    public static String getPrintSize(long size) {
        // 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        double value = (double)size;
        if (value < 1024) {
            return value + "B";
        } else {
            value = new BigDecimal(value / 1024).setScale(2, RoundingMode.DOWN).doubleValue();
        }
        // 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        // 因为还没有到达要使用另一个单位的时候
        // 接下去以此类推
        if (value < 1024) {
            return value + "KB";
        } else {
            value = new BigDecimal(value / 1024).setScale(2, RoundingMode.DOWN).doubleValue();
        }
        if (value < 1024) {
            return value + "MB";
        } else {
            // 否则如果要以GB为单位的，先除于1024再作同样的处理
            value = new BigDecimal(value / 1024).setScale(2, RoundingMode.DOWN).doubleValue();
            return value + "GB";
        }
    }

}
