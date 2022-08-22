package io.shulie.takin.cloud.app.service;

import io.shulie.takin.cloud.model.callback.basic.PressureExample;

/**
 * 施压任务实例服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface PressureExampleService {
    /**
     * 心跳事件
     *
     * @param pressureExampleId 施压任务实例主键
     */
    void onHeartbeat(long pressureExampleId);

    /**
     * 启动事件
     *
     * @param pressureExampleId 施压任务实例主键
     */
    void onStart(long pressureExampleId);

    /**
     * 停止事件
     *
     * @param pressureExampleId 施压任务实例主键
     */
    void onStop(long pressureExampleId);

    /**
     * 异常事件
     *
     * @param pressureExampleId 施压任务实例主键
     * @param errorInfo         错误信息
     */
    void onError(long pressureExampleId, String errorInfo);

    /**
     * 获取回调数据
     *
     * @param pressureExampleId 施压任务实例主键
     * @param callbackUrl       回调地址
     * @return 回调数据
     */
    PressureExample getCallbackData(long pressureExampleId, StringBuilder callbackUrl);
}
