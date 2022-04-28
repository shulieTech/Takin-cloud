package io.shulie.takin.cloud.app.service.impl;

import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.app.util.InfluxUtil;
import io.shulie.takin.cloud.app.util.InfluxWriter;
import io.shulie.takin.cloud.app.service.MetricsService;
import io.shulie.takin.cloud.app.service.JobExampleServer;
import io.shulie.takin.cloud.model.notify.Metrics.MetricsInfo;
import io.shulie.takin.cloud.constant.PressureEngineConstants;

/**
 * 指标服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
@Slf4j(topic = "metrics")
public class MetricsServiceImpl implements MetricsService {
    @Resource
    private InfluxWriter influxWriter;
    @javax.annotation.Resource
    JobExampleServer jobExampleServer;

    /**
     * {@inheritDoc}
     */
    @Override
    public void upload(Long jobExampleId, List<MetricsInfo> metricsList) {
        long timestamp = metricsList.get(0).getTimestamp();
        log.debug("Metrics-Upload({}):接受到的数据:{}", jobExampleId, metricsList);
        log.info("Metrics-Upload({}): 接收到的数据:{}条,时间范围:{},延时:{}",
            jobExampleId, metricsList.size(), timestamp, (System.currentTimeMillis() - timestamp));
        // 回调数据
        jobExampleServer.onHeartbeat(jobExampleId);

        collectorToInfluxdb(jobExampleId, metricsList);
        //collectorService.statisticalIp(sceneId, reportId, tenantId, timestamp, IPUtils.getIP(request));
    }

    public void collectorToInfluxdb(Long jobExampleId, List<MetricsInfo> metricsList) {
        if (CollUtil.isEmpty(metricsList)) {
            return;
        }
        String measurement = InfluxUtil.getMetricsMeasurement(jobExampleId);
        metricsList.stream().filter(Objects::nonNull)
            .peek(metrics -> {
                //判断有没有MD5值
                int strPosition = metrics.getTransaction().lastIndexOf(PressureEngineConstants.TRANSACTION_SPLIT_STR);
                if (strPosition > 0) {
                    String transaction = metrics.getTransaction();
                    metrics.setTransaction(transaction.substring(strPosition + PressureEngineConstants.TRANSACTION_SPLIT_STR.length()));
                } else {
                    metrics.setTransaction(metrics.getTransaction());
                }
            })
            .peek(metrics -> {
                //处理时间戳-纳秒转成毫秒，防止插入influxdb报错
                if (Objects.nonNull(metrics.getTimestamp()) && metrics.getTimestamp() > InfluxUtil.MAX_ACCEPT_TIMESTAMP) {
                    metrics.setTimestamp(metrics.getTimestamp() / 1000000);
                }
                if (metrics.getTimestamp() > InfluxUtil.MAX_ACCEPT_TIMESTAMP) {
                    metrics.setTimestamp(metrics.getTimestamp() / 1000000);
                }
            })
            .map(metrics -> InfluxUtil.toPoint(measurement, metrics.getTimestamp(), metrics))
            .forEach(influxWriter::insert);
    }

}
