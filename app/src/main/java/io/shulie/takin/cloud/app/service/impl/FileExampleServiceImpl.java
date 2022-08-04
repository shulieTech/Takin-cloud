package io.shulie.takin.cloud.app.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.data.entity.FileExampleEntity;
import io.shulie.takin.cloud.app.service.FileExampleService;
import io.shulie.takin.cloud.model.request.file.ProgressRequest;
import io.shulie.takin.cloud.data.service.FileExampleMapperService;

/**
 * TODO
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class FileExampleServiceImpl implements FileExampleService {
    @javax.annotation.Resource(name = "fileExampleMapperServiceImpl")
    FileExampleMapperService fileExampleMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateProgress(List<ProgressRequest> progressList) {
        // 1. 数据更新 [t_file_example]
        for (ProgressRequest t : progressList) {
            boolean updateResult = fileExampleMapper.lambdaUpdate()
                .set(FileExampleEntity::getCompleted, t.getCompleted())
                .set(FileExampleEntity::getCompleteSize, t.getCompleteSize())
                .set(FileExampleEntity::getTotalSize, t.getTotalSize())
                // 数据可以更新的条件
                .isNull(FileExampleEntity::getMessage)
                .lt(Objects.nonNull(t.getCompleteSize()), FileExampleEntity::getCompleteSize, t.getCompleteSize())
                .eq(FileExampleEntity::getCompleted, Boolean.FALSE)
                .update();
            if (updateResult) {
                // TODO 2. 数据入库 [t_callback]
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fail(Long id, String message) {
        // 1. 数据更新 [t_file_example]
        boolean updateResult = fileExampleMapper.lambdaUpdate()
            .set(FileExampleEntity::getMessage, message)
            .set(FileExampleEntity::getId, id)
            // 数据可以更新的条件
            .isNull(FileExampleEntity::getMessage)
            .eq(FileExampleEntity::getCompleted, Boolean.FALSE)
            .update();
        if (updateResult) {
            // TODO 2. 数据入库 [t_callback]
        }
    }

    public boolean saveBatch(List<FileExampleEntity> data) {
        return fileExampleMapper.saveBatch(data);
    }

    @Override
    public List<FileExampleEntity> listExample(long fileId) {
        return fileExampleMapper.lambdaQuery()
            .eq(FileExampleEntity::getFileId, fileId)
            .list();
    }

}
