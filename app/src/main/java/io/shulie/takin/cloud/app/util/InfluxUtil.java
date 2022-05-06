package io.shulie.takin.cloud.app.util;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;
import org.influxdb.BuilderException;
import org.influxdb.annotation.Column;

/**
 * Influx工具类
 *
 * @author qianshui
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 * @date 2020/7/20 下午4:34
 */
public class InfluxUtil {

    public static long MAX_ACCEPT_TIMESTAMP = 9223372036854L;

    /**
     * 实时统计数据表
     */
    @SuppressWarnings("unused")
    public static String getMeasurement(Long jobExampleId) {
        return getMeasurement("pressure", jobExampleId);
    }

    /**
     * 指标数据上报的数据表
     */
    public static String getMetricsMeasurement(Long jobExampleId) {
        return getMeasurement("metrics", jobExampleId);
    }

    /**
     * 拼装influxdb表名
     */
    public static String getMeasurement(String measurementName, Long jobExampleId) {
        return String.format("%s_%s", measurementName, jobExampleId);
    }

    /**
     * 数据转换，将ResponseMetrics转换成influxdb入库对象Point
     */
    public static Point toPoint(String measurement, long time, Object pojo) {
        Point.Builder builder = Point.measurement(measurement)
            .time(time, TimeUnit.MILLISECONDS)
            //当前类的字段添加到数据库
            .addFieldsFromPOJO(pojo)
            .addField("create_time", System.currentTimeMillis());
        Class<?> superclass = pojo.getClass().getSuperclass();
        //父类字段添加到数据库
        addSuperClassFieldsFromPojo(builder, pojo, superclass);
        return builder.build();
    }

    private static void addSuperClassFieldsFromPojo(Point.Builder builder, Object pojo, Class<?> clazz) {
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
            addSuperClassFieldsFromPojo(builder, pojo, clazz.getSuperclass());
        }
    }

    @SuppressWarnings("deprecation")
    private static void addFieldByAttribute(final Point.Builder builder, final Object pojo, final Field field, final Column column, final String fieldName) {
        try {
            Object fieldValue = field.get(pojo);
            if (column.tag()) {
                builder.tag(fieldName, (String)fieldValue);
            } else {
                builder.field(fieldName, fieldValue);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // Can not happen since we use metadata got from the object
            throw new BuilderException("Field " + fieldName + " could not found on class " + pojo.getClass().getSimpleName());
        }
    }
}
