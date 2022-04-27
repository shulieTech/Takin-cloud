package io.shulie.takin.cloud.app.service;

import com.github.pagehelper.PageInfo;
import io.shulie.takin.cloud.app.entity.CallbackEntity;

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
     * 预创建回调日志
     *
     * @param callbackId 回调主键
     * @param url        回调路径
     * @param data       回调内容
     * @return 回调日志主键
     */
    Long createLog(long callbackId, String url, byte[] data);

    /**
     * 填充回调日志
     *
     * @param callbackLogId 回调日志主键
     * @param data          响应内容
     */
    void fillLog(long callbackLogId, byte[] data);
}