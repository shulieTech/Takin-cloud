package io.shulie.takin.cloud.app.service;

import java.util.List;

import io.shulie.takin.cloud.data.entity.FileEntity;
import io.shulie.takin.cloud.model.request.job.file.AnnounceRequest;

/**
 * 文件资源服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface FileService {

    /**
     * 下发命令
     *
     * @param attach         附加数据
     * @param fileList       文件列表
     * @param callbackUrl    回调地址
     * @param watchmanIdList 调度机主键列表
     * @return 文件资源管理-批次主键
     */
    long announce(String attach, String callbackUrl,
        List<Long> watchmanIdList, List<AnnounceRequest.File> fileList);

    /**
     * 获取数据项
     *
     * @param fileId 数据主键
     * @return 数据项
     */
    FileEntity entity(long fileId);

}
