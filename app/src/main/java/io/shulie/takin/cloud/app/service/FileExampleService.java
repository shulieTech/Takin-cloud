package io.shulie.takin.cloud.app.service;

import java.util.List;

import io.shulie.takin.cloud.data.entity.FileExampleEntity;
import io.shulie.takin.cloud.model.request.file.ProgressRequest;

/**
 * 文件资源实例服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface FileExampleService {

    /**
     * 更新进度
     *
     * @param progressList 进度列表
     */
    void updateProgress(List<ProgressRequest> progressList);

    /**
     * 标记为失败
     *
     * @param id      主键
     * @param message 消息
     */
    void fail(Long id, String message);

    /**
     * 批量保存数据
     *
     * @param data 数据
     * @return 操作结果
     */
    boolean saveBatch(List<FileExampleEntity> data);

    /**
     * 获取数据项-文件实例
     *
     * @param fileId 数据主键
     * @return 文件实例数据项
     */
    List<FileExampleEntity> listExample(long fileId);

}
