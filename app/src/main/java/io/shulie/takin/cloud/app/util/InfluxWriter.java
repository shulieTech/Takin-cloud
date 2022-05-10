package io.shulie.takin.cloud.app.util;

import java.util.Map;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.core.collection.CollUtil;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.QueryResult;
import org.influxdb.InfluxDBFactory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
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

    public static BatchPoints batchPoints(String sdatabase) {
        return BatchPoints.database(sdatabase).build();
    }

    @PostConstruct
    public void init() {
        if (StringUtils.isBlank(influxdbUrl)) {
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
            log.error("influxdb写数据异常.\n", ex);
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
            List<QueryResult.Series> series = result.getSeries();
            if (series == null) {continue;}
            for (QueryResult.Series serie : series) {
                List<String> colums = serie.getColumns();
                Map<String, String> tags = serie.getTags();
                List<List<Object>> values = serie.getValues();
                // 封装查询结果
                getResult(colums, values, tags, resultArr);
            }
        }
        return JSONUtil.toList(resultArr.toJSONString(2), clazz);
    }

    public void getResult(List<String> colums, List<List<Object>> values, Map<String, String> tags, JSONArray array) {
        for (List<Object> value : values) {
            JSONObject jsonData = new JSONObject();
            if (tags != null) {jsonData.putAll(tags);}
            for (int i = 0; i < colums.size(); ++i) {
                jsonData.set(colums.get(i), value.get(i));
            }
            array.add(jsonData);
        }
    }

    public <T> T querySingle(String command, Class<T> clazz) {
        List<T> data = query(command, clazz);
        if (CollUtil.isNotEmpty(data)) {return data.get(0);}
        return null;
    }

    /**
     * 设置数据保存策略
     * defalut 策略名 /database 数据库名/ 30d 数据保存时限30天/ 1
     * 副本个数为1/ 结尾DEFAULT 表示 设为默认的策略
     */
    public void createRetentionPolicy() {
        String command = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s DEFAULT",
            "defalut", database, "30d", 1);
        influx.query(new Query(command, database));
    }

    public String getInfluxdbUrl() {
        return influxdbUrl;
    }
}
