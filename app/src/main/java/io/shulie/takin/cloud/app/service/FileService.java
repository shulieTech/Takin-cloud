package io.shulie.takin.cloud.app.service;

import java.util.List;

import io.shulie.takin.cloud.model.request.file.AnnounceRequest;
import io.shulie.takin.cloud.model.request.file.ProgressRequest;

/**
 * 文件资源服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface FileService {

    /**
     * 下发命令
     *
     * @param fileList       文件列表
     * @param watchmanIdList 调度机主键列表
     * @return 文件资源管理-批次主键
     */
    long announce(List<Long> watchmanIdList, List<AnnounceRequest.File> fileList);

    /**
     * 更新文件资源进度
     *
     * @param progressList 进度列表
     */
    void updateProgress(List<ProgressRequest> progressList);
}
