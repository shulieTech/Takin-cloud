package io.shulie.takin.cloud.app.service;

import java.util.List;

import io.shulie.takin.cloud.model.request.MetricsInfo;

/**
 * 指标数据服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface MetricsService {
    /**
     * 上报
     *
     * @param jobId        任务主键
     * @param jobExampleId 任务实例主键
     * @param metricsList  数据集合
     * @param ip           请求IP
     */
    void upload(Long jobId, Long jobExampleId, List<MetricsInfo> metricsList, String ip);
}
