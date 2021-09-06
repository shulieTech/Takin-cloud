package io.shulie.takin.cloud.biz.service.sla;

import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;

/**
 * @author qianshui
 * @date 2020/4/20 下午4:47
 */
public interface SlaService {

    Boolean buildWarn(SendMetricsEvent metricsEvnet);

    void removeMap(Long sceneId);

    void cacheData(Long sceneId);
}
