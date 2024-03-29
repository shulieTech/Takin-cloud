package io.shulie.takin.cloud.app.service;

import java.util.List;
import java.util.Collections;

import io.shulie.takin.cloud.data.entity.MetricsEntity;
import io.shulie.takin.cloud.data.entity.ThreadConfigEntity;
import io.shulie.takin.cloud.constant.enums.ThreadGroupType;
import io.shulie.takin.cloud.data.entity.ThreadConfigExampleEntity;

/**
 * 施压任务配置服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface PressureConfigService {
    /**
     * 获取指标配置列表
     *
     * @param pressureId 施压任务主键
     * @return 指标配置列表
     */
    List<MetricsEntity> metricsList(long pressureId);

    /**
     * 获取线程组配置列表
     *
     * @param pressureId 施压任务主键
     * @return 线程组配置列表
     */
    List<ThreadConfigExampleEntity> threadList(long pressureId);

    /**
     * 获取线程组配置实例
     *
     * @param pressureId 施压任务主键
     * @param ref        关键词
     * @return 线程组配置
     */
    List<ThreadConfigExampleEntity> threadExampleItem(long pressureId, String ref);

    /**
     * 修改线程组
     *
     * @param threadConfigExampleId 线程组配置实例主键
     * @param type                  线程组类型
     * @param context               修改内容
     */
    void modifThreadConfigExample(long threadConfigExampleId, ThreadGroupType type, String context);

    /**
     * 创建指标配置
     *
     * @param metricsEntityList 指标配置
     */
    void createMetrics(List<MetricsEntity> metricsEntityList);

    /**
     * 创建指标配置
     *
     * @param metricsEntity 指标配置
     */
    @SuppressWarnings("unused")
    default void createMetrics(MetricsEntity metricsEntity) {
        createMetrics(Collections.singletonList(metricsEntity));
    }

    /**
     * 创建线程组配置
     *
     * @param metricsEntityList 线程组配置
     */
    void createThread(List<ThreadConfigEntity> metricsEntityList);

    /**
     * 创建线程组配置
     *
     * @param threadConfigEntity 线程组配置
     */
    @SuppressWarnings("unused")
    default void createThread(ThreadConfigEntity threadConfigEntity) {
        createThread(Collections.singletonList(threadConfigEntity));
    }

    /**
     * 创建线程组配置实例
     *
     * @param threadConfigExampleEntityList 线程组配置实例
     */
    void createThreadExample(List<ThreadConfigExampleEntity> threadConfigExampleEntityList);

    /**
     * 创建线程组配置实例
     *
     * @param threadConfigExampleEntity 线程组配置实例
     */
    @SuppressWarnings("unused")
    default void createThreadExample(ThreadConfigExampleEntity threadConfigExampleEntity) {
        createThreadExample(Collections.singletonList(threadConfigExampleEntity));
    }

}
