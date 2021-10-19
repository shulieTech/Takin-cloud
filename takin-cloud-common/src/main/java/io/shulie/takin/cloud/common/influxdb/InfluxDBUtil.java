package io.shulie.takin.cloud.common.influxdb;

import io.shulie.takin.cloud.common.utils.CollectorUtil;
import org.influxdb.BuilderException;
import org.influxdb.annotation.Column;
import org.influxdb.dto.Point;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * @author qianshui
 * @date 2020/7/20 下午4:34
 */
public class InfluxDBUtil {

    /**
     * 实时统计数据表
     */
    public static String getMeasurement(Long sceneId, Long reportId, Long customerId) {
        return getMeasurement("pressure", sceneId, reportId, customerId);
    }

    /**
     * jmeter上报的数据表
     */
    public static String getMetricsMeasurement(Long sceneId, Long reportId, Long customerId) {
        return getMeasurement("metrics", sceneId, reportId, customerId);
    }

    /**
     * 拼装influxdb表名
     */
    public static String getMeasurement(String measurementName, Long sceneId, Long reportId, Long customerId) {
        if (customerId == null) {
            return String.format("%s_%s_%s", measurementName, sceneId, reportId);
        }
        return String.format("%s_%s_%s_%s", measurementName, sceneId, reportId, customerId);
    }

    /**
     * 数据转换，将ResponseMetrics转换成influxdb入库对象Point
     */
    public static Point toPoint(String measurement, long time, Object pojo) {
        Point.Builder builder = Point.measurement(measurement)
                .time(CollectorUtil.getTimeWindowTime(time), TimeUnit.MILLISECONDS)
                //当前类的字段添加到数据库
                .addFieldsFromPOJO(pojo)
                .addField("create_time", System.currentTimeMillis());
        Class superclass = pojo.getClass().getSuperclass();
        //父类字段添加到数据库
        addSuperClassFieldsFromPOJO(builder, pojo, superclass);
        return builder.build();
    }

    private static void addSuperClassFieldsFromPOJO(Point.Builder builder, Object pojo, Class clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }
            field.setAccessible(true);
            String fieldName = column.name();
            addFieldByAttribute(builder, pojo, field, column, fieldName);
        }
        if (clazz != Object.class) {
            addSuperClassFieldsFromPOJO(builder, pojo, clazz.getSuperclass());
        }
    }

    private static void addFieldByAttribute(final Point.Builder builder, final Object pojo, final Field field, final Column column, final String fieldName) {
        try {
            Object fieldValue = field.get(pojo);
            if (column.tag()) {
                builder.tag(fieldName, (String) fieldValue);
            } else {
                builder.field(fieldName, fieldValue);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // Can not happen since we use metadata got from the object
            throw new BuilderException("Field " + fieldName + " could not found on class " + pojo.getClass().getSimpleName());
        }
    }
}
