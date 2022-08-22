package io.shulie.takin.cloud.app.service;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * 回调日志服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface CallbackLogService {
    /**
     * 统计日志总数
     *
     * @param callbackId 回调主键
     * @return 计数
     */
    Long count(long callbackId);

    /**
     * 预创建回调日志
     *
     * @param callbackId 回调主键
     * @param type       回调类型
     * @param url        回调路径
     * @param data       回调内容
     * @return 回调日志主键
     */
    Long create(long callbackId, Integer type, String url, byte[] data);

    /**
     * 填充回调日志
     *
     * @param callbackLogId 回调日志主键
     * @param data          响应内容
     */
    void fill(long callbackLogId, byte[] data);

    /**
     * 填充回调日志
     *
     * @param callbackLogId 回调日志主键
     * @param data          响应内容
     */
    default void fill(long callbackLogId, String data) {
        fill(callbackLogId, CharSequenceUtil.utf8Bytes(data));
    }

}
