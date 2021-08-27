package io.shulie.takin.cloud.common.influxdb;

/**
 * @ClassName InfluxDBUtil
 * @Description
 * @Author qianshui
 * @Date 2020/7/20 下午4:34
 */
public class InfluxDBUtil {

    public static String getMeasurement(Long sceneId, Long reportId, Long customerId) {
        if (customerId == null || customerId < 0) {
            return String.format("pressure_%s_%s", sceneId, reportId);
        }
        return String.format("pressure_%s_%s_%s", sceneId, reportId, customerId);
    }
}
