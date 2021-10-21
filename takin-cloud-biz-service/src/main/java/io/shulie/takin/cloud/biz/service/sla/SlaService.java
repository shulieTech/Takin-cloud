package io.shulie.takin.cloud.biz.service.sla;

import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;

/**
 * @author qianshui
 * @date 2020/4/20 下午4:47
 */
public interface SlaService {
    /**
     * 绑定警告
     *
     * @param metricsEvent -
     * @return 操作结果
     */
    Boolean buildWarn(SendMetricsEvent metricsEvent);

    /**
     * 移除Map
     *
     * @param sceneId 场景主键
     */
    void removeMap(Long sceneId);

    /**
     * 缓存数据
     *
     * @param sceneId 场景主键
     */
    void cacheData(Long sceneId);
}
