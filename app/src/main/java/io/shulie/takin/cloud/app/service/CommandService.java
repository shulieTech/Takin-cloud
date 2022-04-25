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
     * @param entity 参数
     */
    void graspResource(ResourceExampleEntity entity);

    /**
     * 释放资源实例
     *
     * @param entity 参数
     */
    void releaseResource(ResourceExampleEntity entity);

    /**
     * 启动应用程序
     *
     * @param obj 启动参数
     */
    void startApplication(Object obj);

}
