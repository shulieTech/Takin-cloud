package io.shulie.takin.cloud.app.service;

/**
 * 任务服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface JobService {

    /**
     * 启动
     *
     * @param jobInfo 任务信息
     * @return 任务主键
     */
    String start(Object jobInfo);

    /**
     * 停止
     *
     * @param taskId 任务主键
     */
    void stop(long taskId);

    /**
     * 查看配置
     *
     * @param taskId 任务主键
     * @return 配置内容
     */
    Object getConfig(long taskId);

    /**
     * 修改配置
     *
     * @param taskId  任务主键
     * @param context 配置内容
     */
    void modifyConfig(long taskId, Object context);
}
