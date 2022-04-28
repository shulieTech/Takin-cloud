package io.shulie.takin.cloud.app.service;

import io.shulie.takin.cloud.model.callback.basic.JobExample;

/**
 * 任务实例服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface JobExampleServer {
    /**
     * 心跳事件
     *
     * @param id 资源实例主键
     */
    void onHeartbeat(long id);

    /**
     * 启动事件
     *
     * @param id 资源实例主键
     */
    void onStart(long id);

    /**
     * 停止事件
     *
     * @param id 资源实例主键
     */
    void onStop(long id);

    /**
     * 异常事件
     *
     * @param id        资源实例主键
     * @param errorInfo 错误信息
     */
    void onError(long id, String errorInfo);

    /**
     * 获取回调数据
     *
     * @param jobExampleId 任务实例主键
     * @param callbackUrl  回调地址
     * @return 回调数据
     */
    JobExample getCallbackData(long jobExampleId, StringBuffer callbackUrl);
}
