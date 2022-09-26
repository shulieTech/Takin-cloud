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
 */
public class InfluxUtil {
    private InfluxUtil() {}

    public static final long MAX_ACCEPT_TIMESTAMP = 9223372036854L;

    /**
     * 指标数据上报的数据表
     *
     * @param pressureExampleId 施压任务实例主键
     * @return 表名
     */
    public static String getMetricsMeasurement(Long pressureExampleId) {
        return getMeasurement("metrics", pressureExampleId);
    }

    /**
     * 拼装influxdb表名
     *
     * @param pressureExampleId 施压任务实例主键
     * @param measurementName   名称
     * @return 表名
     */
    public static String getMeasurement(String measurementName, Long pressureExampleId) {
        return String.format("%s_%s", measurementName, pressureExampleId);
    }

    /**
     * 数据转换，将ResponseMetrics转换成influxdb入库对象Point
     *
     * @param measurement 表名
     * @param time        时间
     * @param pojo        对象
     * @return point
     */
    public static Point toPoint(String measurement, long time, Object pojo) {
        Point.Builder builder = Point.measurement(measurement)
            .time(time, TimeUnit.MILLISECONDS)
            .addFieldsFromPOJO(pojo)
            .addField("create_time", System.currentTimeMillis());
        Class<?> superclass = pojo.getClass().getSuperclass();
        //父类字段添加到数据库
        addSuperClassFieldsFromPojo(builder, pojo, superclass);
        return builder.build();
    }

    /**
     * @param builder -
     * @param pojo    -
     * @param clazz   -
     */
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

    /**
     * @param builder   -
     * @param pojo      -
     * @param field     -
     * @param column    -
     * @param fieldName -
     */
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
