package io.shulie.takin.cloud.app.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

import io.shulie.takin.cloud.app.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import io.shulie.takin.cloud.app.util.InfluxUtil;
import io.shulie.takin.cloud.app.util.InfluxWriter;
import io.shulie.takin.cloud.app.util.CollectorUtil;
import io.shulie.takin.cloud.app.service.SlaService;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.data.entity.SlaEventEntity;
import io.shulie.takin.cloud.app.service.PressureMetricsService;
import io.shulie.takin.cloud.constant.PressureEngineConstants;
import io.shulie.takin.cloud.app.service.PressureExampleService;
import io.shulie.takin.cloud.model.request.job.pressure.MetricsInfo;

import static com.alibaba.fastjson.JSON.*;

/**
 * 施压任务指标服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
@Slf4j(topic = "metrics")
public class PressurePressureMetricsServiceImpl implements PressureMetricsService {
    @javax.annotation.Resource
    SlaService slaService;
    @javax.annotation.Resource
    JsonService jsonService;
    @javax.annotation.Resource
    private InfluxWriter influxWriter;
    @javax.annotation.Resource
    PressureExampleService pressureExampleService;
    @javax.annotation.Resource
    RedisTemplate<String, Object> stringRedisTemplate;
    @Value("${substring.ptl.result:false}")
    private Boolean substringPtlResult;
    /**
     * {@inheritDoc}
     */
    @Override
    public void upload(Long pressureId, Long pressureExampleId, List<MetricsInfo> metricsList, String ip) {
        long timestamp = metricsList.get(0).getTimestamp();
        log.debug("Metrics-Upload({}-{}):接受到的数据:{}", pressureId, pressureExampleId, metricsList);
        //如果点击了压测停止或者主动停止，则丢弃后续的数据
        if(Boolean.TRUE.equals(substringPtlResult)
                && Boolean.TRUE.equals(stringRedisTemplate.hasKey(String.format(RedisKeyUtil.stopSceneKey, pressureId)))) {
            log.warn("Metrics-Upload({}-{}): 接收到的数据:{}条,压测已停止，数据将会被丢弃......", pressureId, pressureExampleId, metricsList.size());
            return;
        }
        log.info("Metrics-Upload({}-{}): 接收到的数据:{}条,时间范围:{},延时:{}", pressureId, pressureExampleId,
            metricsList.size(), timestamp, (System.currentTimeMillis() - timestamp));
        try {
            // 回调数据
            pressureExampleService.onHeartbeat(pressureExampleId);
            // 写入InfluxDB
            collectorToInfluxdb(pressureId, metricsList);
            // 统计每个时间窗口pod调用数量
            statisticalIp(pressureId, timestamp, ip);
            // SLA检查
            List<SlaEventEntity> check = slaService.check(pressureId, pressureExampleId, metricsList);
            // 进行通知
            slaService.event(pressureId, pressureExampleId, check);
        }catch (Throwable e) {
            log.error("upload error,{}-{}", pressureId, pressureExampleId);
            log.error("upload error,data detail is {}", metricsList.toString());
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void collectorToInfluxdb(Long pressureId, List<MetricsInfo> metricsList) {
        try {
            if (CollUtil.isEmpty(metricsList)) {
                return;
            }
            String measurement = InfluxUtil.getMetricsMeasurement(pressureId);
            List<MetricsInfo> metricsInfoList = metricsList.stream().filter(Objects::nonNull).collect(Collectors.toList());
            metricsInfoList.forEach(metrics -> {
                //判断有没有MD5值
                int strPosition = metrics.getTransaction().lastIndexOf(PressureEngineConstants.TRANSACTION_SPLIT_STR);
                if (strPosition > 0) {
                    String transaction = metrics.getTransaction();
                    metrics.setTransaction(transaction.substring(strPosition + PressureEngineConstants.TRANSACTION_SPLIT_STR.length()));
                    metrics.setTestName((transaction.substring(0, strPosition)));
                } else {
                    metrics.setTransaction(metrics.getTransaction());
                    metrics.setTestName(metrics.getTransaction());
                }
            });
            metricsInfoList.stream().map(metrics -> {
                //处理时间戳-纳秒转成毫秒，防止插入influxdb报错
                if (Objects.nonNull(metrics.getTime()) && metrics.getTime() > InfluxUtil.MAX_ACCEPT_TIMESTAMP) {
                    metrics.setTimestamp(metrics.getTimestamp() / 1000000);
                }
                if (Objects.nonNull(metrics.getTimestamp()) && metrics.getTimestamp() > InfluxUtil.MAX_ACCEPT_TIMESTAMP) {
                    metrics.setTimestamp(metrics.getTimestamp() / 1000000);
                }
                return InfluxUtil.toPoint(measurement, metrics.getTimestamp(), metrics);
            }).forEach(influxWriter::insert);
        } catch (Throwable e) {
            log.error("collectorToInfluxdb error", e);
            throw e;
        }

    }

    /**
     * {@inheritDoc}
     */
    public void statisticalIp(Long pressureId, long time, String ip) {
        // 时间窗口缓存Key
        String redisKey = String.format("%s:%s", getKey(pressureId), "windowsTime");
        //  获取当前时间窗口
        String hashKey = String.valueOf(CollectorUtil.getTimeWindowTime(time));
        // 声明IP列表
        List<String> ipList = new ArrayList<>();
        // 如果缓存不存在
        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            ipList.add(ip);
            stringRedisTemplate.opsForHash().put(redisKey, hashKey, jsonService.writeValueAsString(ipList));
            stringRedisTemplate.expire(redisKey, 1, TimeUnit.DAYS);
        } else {
            Object cacheData = stringRedisTemplate.opsForHash().get(redisKey, hashKey);
            if (cacheData instanceof List) {
                ipList = ((List<?>)cacheData).stream().filter(String.class::isInstance)
                    .map(Object::toString).collect(Collectors.toList());
            }
            stringRedisTemplate.opsForHash().put(redisKey, hashKey, jsonService.writeValueAsString(ipList));
        }
    }

    /**
     * 获取施压任务对应的Redis的Key
     *
     * @param pressureId 施压任务主键
     * @return Redis的Key
     */
    protected String getKey(Long pressureId) {
        return String.format("COLLECTOR:TASK:%s", pressureId);
    }

}
