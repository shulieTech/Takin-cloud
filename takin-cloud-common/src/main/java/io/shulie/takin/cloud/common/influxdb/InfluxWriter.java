package io.shulie.takin.cloud.common.influxdb;

import java.util.Map;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.util.StrUtil;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.QueryResult;
import org.springframework.stereotype.Component;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;

import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-20 14:25
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class InfluxWriter {

    /**
     * 连接地址
     */
    @Value("${spring.influxdb.url:}")
    private String influxdbUrl;

    /**
     * 用户名
     */
    @Value("${spring.influxdb.user:}")
    private String userName;

    /**
     * 密码
     */
    @Value("${spring.influxdb.password:}")
    private String password;

    /**
     * 数据库库名
     */
    @Value("${spring.influxdb.database:}")
    private String database;

    private InfluxDB influx;

    public static BatchPoints batchPoints(String database) {
        return BatchPoints.database(database).build();
    }

    @PostConstruct
    public void init() {
        if (StrUtil.isBlank(influxdbUrl)) {
            return;
        }
        influx = InfluxDBFactory.connect(influxdbUrl, userName, password);
        influx.enableBatch(1000, 40, TimeUnit.MILLISECONDS);
    }

    /**
     * 插入批量数据
     *
     * @param batchPoints 批量数据点
     */
    public void writeBatchPoint(BatchPoints batchPoints) {
        influx.write(batchPoints);
    }

    /**
     * 插入数据
     *
     * @param measurement 表名
     * @param tags        标签
     * @param fields      字段
     * @param time        时间
     * @return -
     */
    public boolean insert(String measurement, Map<String, String> tags, Map<String, Object> fields, long time) {
        Point.Builder builder = Point.measurement(measurement);
        builder.tag(tags);
        builder.fields(fields);
        if (time > 0) {
            builder.time(time, TimeUnit.MILLISECONDS);
        }
        return insert(builder.build());
    }

    /**
     * 插入数据
     */
    public boolean insert(Point point) {
        try {
            influx.write(database, "", point);
        } catch (Exception ex) {
            log.error("异常代码【{}】,异常内容：influxdb写数据异常 --> 异常信息: {}",
                TakinCloudExceptionEnum.TASK_RUNNING_RECEIVE_PT_DATA_ERROR, ex);
            return false;
        }
        return true;
    }

    /**
     * 查询数据
     *
     * @return -
     */
    public List<QueryResult.Result> select(String command) {
        QueryResult queryResult = influx.query(new Query(command, database));
        return queryResult.getResults();
    }

    /**
     * 创建数据库
     *
     * @param dbName 数据库名称
     */
    public void createDatabase(String dbName) {
        influx.setDatabase(dbName);
    }

    /**
     * 封装查询结果
     *
     * @param command 命令
     * @param clazz   结果集合的泛型类
     * @param <T>     泛型
     * @return -
     */
    public <T> List<T> query(String command, Class<T> clazz) {
        List<QueryResult.Result> results = select(command);

        JSONArray resultArr = new JSONArray();
        for (QueryResult.Result result : results) {
            List<QueryResult.Series> seriesList = result.getSeries();
            if (seriesList == null) {
                continue;
            }
            for (QueryResult.Series series : seriesList) {
                List<List<Object>> values = series.getValues();
                List<String> columns = series.getColumns();
                Map<String, String> tags = series.getTags();

                // 封装查询结果
                for (List<Object> value : values) {
                    JSONObject jsonData = new JSONObject();
                    if (tags != null && tags.keySet().size() > 0) {
                        jsonData.putAll(tags);
                    }
                    for (int j = 0; j < columns.size(); ++j) {
                        jsonData.put(columns.get(j), value.get(j));
                    }
                    resultArr.add(jsonData);
                }
            }
        }
        return JSONObject.parseArray(resultArr.toJSONString(), clazz);
    }

    public <T> T querySingle(String command, Class<T> clazz) {
        List<T> data = query(command, clazz);
        if (CollectionUtils.isNotEmpty(data)) {
            return data.get(0);
        }
        return null;
    }

    /**
     * 设置数据保存策略
     * default 策略名 /database 数据库名/ 30d 数据保存时限30天/ 1
     * 副本个数为1/ 结尾DEFAULT 表示 设为默认的策略
     */
    public void createRetentionPolicy() {
        String command = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s DEFAULT",
            "default", database, "30d", 1);
        influx.query(new Query(command, database));
    }

    public String getInfluxdbUrl() {
        return influxdbUrl;
    }
}
