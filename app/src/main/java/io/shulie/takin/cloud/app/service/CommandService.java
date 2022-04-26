package io.shulie.takin.cloud.app.service;

import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;

/**
 * 命令服务
 * <p>用于下发命令</p>
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface CommandService {

    /**
     * 创建资源实例
     *
     * @param resourceExampleId 资源实例主键
     */
    void graspResource(long resourceExampleId);

    /**
     * 释放资源实例
     *
     * @param resourceExampleId 资源实例主键
     */
    void releaseResource(long resourceExampleId);

    /**
     * 启动应用程序
     *
     * @param jobExampleId 任务实例主键
     */
    void startApplication(long jobExampleId);

    /**
     * 停止应用程序
     *
     * @param jobExampleId 任务实例主键
     */
    void stopApplication(long jobExampleId);

    /**
     * 更新配置
     *
     * @param jobId 任务主键
     */
    void updateConfig(long jobId);

}
