package io.shulie.takin.cloud.app.service.impl;

import java.util.List;
import java.util.Objects;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import io.shulie.takin.cloud.data.entity.FileEntity;
import io.shulie.takin.cloud.app.service.FileService;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.data.entity.FileExampleEntity;
import io.shulie.takin.cloud.app.service.FileExampleService;
import io.shulie.takin.cloud.model.callback.file.ProgressReport;
import io.shulie.takin.cloud.data.service.FileExampleMapperService;
import io.shulie.takin.cloud.model.request.job.file.ProgressRequest;

/**
 * 文件实例服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class FileExampleServiceImpl implements FileExampleService {
    @Lazy
    @javax.annotation.Resource
    FileService fileService;
    @javax.annotation.Resource
    JsonService jsonService;
    @javax.annotation.Resource
    CallbackService callbackService;
    @javax.annotation.Resource(name = "fileExampleMapperServiceImpl")
    FileExampleMapperService fileExampleMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateProgress(ProgressRequest progress) {
        // 1. 数据更新 [t_file_example]
        boolean updateResult = fileExampleMapper.lambdaUpdate()
            // ------ set
            .set(FileExampleEntity::getCompleted, progress.getCompleted())
            .set(FileExampleEntity::getCompleteSize, progress.getCompleteSize())
            .set(Objects.nonNull(progress.getTotalSize()), FileExampleEntity::getTotalSize, progress.getTotalSize())
            // ------ where
            .isNull(FileExampleEntity::getMessage)
            .isNull(FileExampleEntity::getCompleted)
            .eq(FileExampleEntity::getId, progress.getId())
            .le(Objects.nonNull(progress.getCompleteSize()), FileExampleEntity::getCompleteSize, progress.getCompleteSize())
            .update();
        if (updateResult) {
            // 进度上报
            progressReport(progress.getId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fail(Long id, String message) {
        // 0. 数据长度处理
        message = CharSequenceUtil.subWithLength(message, 0, 255);
        // 1. 数据更新 [t_file_example]
        boolean updateResult = fileExampleMapper.lambdaUpdate()
            // set
            .set(FileExampleEntity::getMessage, message)
            .set(FileExampleEntity::getCompleted, false)
            // where
            .eq(FileExampleEntity::getId, id)
            .isNull(FileExampleEntity::getMessage)
            .isNull(FileExampleEntity::getCompleted)
            .update();
        if (updateResult) {
            // 进度上报
            progressReport(id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saveBatch(List<FileExampleEntity> data) {
        return fileExampleMapper.saveBatch(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FileExampleEntity> listExample(long fileId) {
        return fileExampleMapper.lambdaQuery().eq(FileExampleEntity::getFileId, fileId).list();
    }

    /**
     * 进度上报
     *
     * @param fileExampleId 文件实例主键
     */
    public void progressReport(Long fileExampleId) {
        FileExampleEntity fileExampleEntity = fileExampleMapper.getById(fileExampleId);
        if (Objects.nonNull(fileExampleEntity)) {
            FileEntity fileEntity = fileService.entity(fileExampleEntity.getFileId());
            if (Objects.nonNull(fileEntity)) {
                ProgressReport progressReport = new ProgressReport();
                progressReport.setData(new ProgressReport.Data()
                    .setPath(fileExampleEntity.getPath())
                    .setAttach(fileExampleEntity.getAttach())
                    .setMessage(fileExampleEntity.getMessage())
                    .setComplete(fileExampleEntity.getCompleted())
                    .setProgress(fileExampleEntity.getCompleteSize() + "")
                );
                callbackService.create(fileEntity.getCallbackUrl(), CallbackType.FILE_RESOURCE_PROGRESS,
                    StrUtil.utf8Str(jsonService.writeValueAsString(progressReport)));
            }
        }
    }

}
