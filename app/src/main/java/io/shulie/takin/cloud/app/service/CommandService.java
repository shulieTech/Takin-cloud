package io.shulie.takin.cloud.app.service;

import com.github.pagehelper.PageInfo;

import io.shulie.takin.cloud.app.entity.CommandEntity;

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
     * @param resourceId 资源主键
     */
    void graspResource(long resourceId);

    /**
     * 释放资源实例
     *
     * @param resourceId 资源主键
     */
    void releaseResource(long resourceId);

    /**
     * 启动应用程序
     *
     * @param jobId 任务主键
     */
    void startApplication(long jobId);

    /**
     * 停止应用程序
     *
     * @param jobId 任务主键
     */
    void stopApplication(long jobId);

    /**
     * 更新配置
     *
     * @param jobId 任务主键
     */
    void updateConfig(long jobId);

    /**
     * 命令确认
     *
     * @param id      命令主键
     * @param type    ack类型
     * @param message ack内容
     * @return true/false
     */
    boolean ack(long id, String type, String message);

    /**
     * 取出一定数量的命令
     *
     * @param watchmanId 调度主键
     * @param number     需要取出的数量
     * @return 命令集合
     */
    PageInfo<CommandEntity> range(long watchmanId, int number);
}
