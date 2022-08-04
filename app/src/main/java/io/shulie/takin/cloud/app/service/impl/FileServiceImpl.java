package io.shulie.takin.cloud.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.app.service.FileService;
import io.shulie.takin.cloud.model.request.file.AnnounceRequest;
import io.shulie.takin.cloud.model.request.file.ProgressRequest;

/**
 * 文件资源服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class FileServiceImpl implements FileService {
    @Override
    public long announce(List<Long> watchmanIdList, List<AnnounceRequest.File> fileList) {
        /* TODO
            1. 数据入库 [t_file]
            2. 数据入库 [t_file_example]
            3. 数据入库 [t_command]
         */
        return 0;
    }

    @Override
    public void updateProgress(List<ProgressRequest> progressList) {
        /* TODO
            1. 数据更新 [t_file_example]
            2. 数据入库 [t_callback]
         */
    }

}
