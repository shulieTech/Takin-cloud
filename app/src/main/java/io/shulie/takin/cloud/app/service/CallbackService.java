package io.shulie.takin.cloud.app.service;

import java.nio.charset.StandardCharsets;

import com.github.pagehelper.PageInfo;

import io.shulie.takin.cloud.data.entity.CallbackEntity;
import io.shulie.takin.cloud.constant.enums.CallbackType;

/**
 * 回调服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface CallbackService {
    /**
     * 分页获取
     *
     * @param pageIndex   分页页码
     * @param pageSize    分页容量
     * @param isCompleted 是否完成
     * @return 集合数据
     */
    PageInfo<CallbackEntity> list(int pageIndex, int pageSize, boolean isCompleted);

    /**
     * 分页获取未完成的列表
     *
     * @param pageIndex 分页页码
     * @param pageSize  分页容量
     * @return 集合数据
     */
    default PageInfo<CallbackEntity> listNotCompleted(int pageIndex, int pageSize) {
        return list(pageIndex, pageSize, false);
    }

    /**
     * 创建回调
     *
     * @param url     回调路径
     * @param content 回调内容
     */
    default void create(String url, byte[] content) {
        create(url, null, content);
    }

    /**
     * 创建回调
     *
     * @param url     回调路径
     * @param type    回调类型
     * @param content 回调内容
     */
    void create(String url, CallbackType type, byte[] content);

    /**
     * 创建回调
     *
     * @param url     回调路径
     * @param content 回调内容
     */
    default void create(String url, String content) {
        create(url, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 创建回调
     *
     * @param url     回调路径
     * @param content 回调内容
     */
    default void create(String url, CallbackType type, String content) {
        create(url, type, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 回调web
     *
     * @param id      回调主键
     * @param url     回调地址
     * @param type    回调类型
     * @param content 回调内容
     */
    void callback(Long id, String url, Integer type, byte[] content);

    /**
     * 更新完成状态
     *
     * @param callbackId 回调主键
     * @param completed  是否完成
     */
    void updateCompleted(long callbackId, Boolean completed);

    /**
     * 更新阈值时间
     *
     * @param callbackId 回调主键
     */
    void updateThresholdTime(long callbackId);
}