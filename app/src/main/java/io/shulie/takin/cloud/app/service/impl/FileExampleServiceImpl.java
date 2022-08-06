package io.shulie.takin.cloud.app.service.impl;

import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import io.shulie.takin.cloud.data.entity.FileEntity;
import io.shulie.takin.cloud.app.service.FileService;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.data.entity.FileExampleEntity;
import io.shulie.takin.cloud.app.service.FileExampleService;
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
    public void updateProgress(List<ProgressRequest> progressList) {
        List<Long> callbackIdList = new ArrayList<>(progressList.size());
        // 1. 数据更新 [t_file_example]
        for (ProgressRequest t : progressList) {
            boolean updateResult = fileExampleMapper.lambdaUpdate()
                // ------ set
                .set(FileExampleEntity::getCompleted, t.getCompleted())
                .set(FileExampleEntity::getCompleteSize, t.getCompleteSize())
                .set(FileExampleEntity::getTotalSize, t.getTotalSize())
                // ------ where
                .eq(FileExampleEntity::getId, t.getId())
                .isNull(FileExampleEntity::getMessage)
                .lt(Objects.nonNull(t.getCompleteSize()), FileExampleEntity::getCompleteSize, t.getCompleteSize())
                .eq(FileExampleEntity::getCompleted, Boolean.FALSE)
                .update();
            if (updateResult) {
                callbackIdList.add(t.getId());
            }
        }
        // 2. 数据入库 [t_callback]
        if (CollUtil.isNotEmpty(callbackIdList)) {
            // 2.1 获取数据
            Map<Long, List<FileExampleEntity>> callbackData = fileExampleMapper.lambdaQuery()
                .in(FileExampleEntity::getId, callbackIdList).list().stream()
                .collect(Collectors.groupingBy(FileExampleEntity::getFileId));
            // 2.2 数据分组
            callbackData.forEach((k, v) -> {
                // 2.3 获取文件
                FileEntity fileEntity = fileService.entity(k);
                // 2.4 创建回调
                if (fileEntity != null && CollUtil.isNotEmpty(v)) {
                    callbackService.create(fileEntity.getCallbackUrl(),
                        StrUtil.utf8Str(jsonService.writeValueAsString(v)));
                }
            });
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
            // where
            .eq(FileExampleEntity::getId, id)
            .isNull(FileExampleEntity::getMessage)
            .eq(FileExampleEntity::getCompleted, Boolean.FALSE)
            .update();
        if (updateResult) {
            // 2. 数据入库 [t_callback]
            // 2.1 获取文件实例
            FileExampleEntity fileExampleEntity = fileExampleMapper.getById(id);
            // 2.2 获取文件
            FileEntity fileEntity = fileService.entity(fileExampleEntity.getFileId());
            if (fileEntity != null) {
                // 2.3 获取相关的所有文件实例
                List<FileExampleEntity> callbackData = fileExampleMapper.lambdaQuery()
                    .eq(FileExampleEntity::getFileId, fileExampleEntity.getFileId()).list();
                // 2.4 创建回调
                if (CollUtil.isNotEmpty(callbackData)) {
                    callbackService.create(fileEntity.getCallbackUrl(),
                        StrUtil.utf8Str(jsonService.writeValueAsString(callbackData)));
                }
            }
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

}
