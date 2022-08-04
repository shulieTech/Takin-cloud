package io.shulie.takin.cloud.app.service.impl;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.data.entity.FileEntity;
import io.shulie.takin.cloud.app.service.FileService;
import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.data.entity.FileExampleEntity;
import io.shulie.takin.cloud.app.service.FileExampleService;
import io.shulie.takin.cloud.data.service.FileMapperService;
import io.shulie.takin.cloud.model.request.file.AnnounceRequest;

/**
 * 文件资源服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class FileServiceImpl implements FileService {
    @javax.annotation.Resource
    CommandService commandService;
    @javax.annotation.Resource
    FileExampleService fileExampleService;
    @javax.annotation.Resource(name = "fileMapperServiceImpl")
    FileMapperService fileMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public long announce(String callbackUrl, List<Long> watchmanIdList, List<AnnounceRequest.File> fileList) {
        // 1. 数据入库 [t_file]
        FileEntity fileEntity = new FileEntity();
        fileMapper.save(fileEntity);
        // 2. 数据入库 [t_file_example]
        List<FileExampleEntity> fileExampleEntityList = new ArrayList<>(fileList.size() * watchmanIdList.size());
        fileList.forEach(t -> watchmanIdList.forEach(c -> fileExampleEntityList.add(new FileExampleEntity()
            .setDownloadUrl(t.getDownloadUrl())
            .setFileId(fileEntity.getId())
            .setPath(t.getPath())
            .setSign(t.getSign())
            .setWatchmanId(c))));
        boolean exampleSaveResult = fileExampleService.saveBatch(fileExampleEntityList);
        if (exampleSaveResult) {
            // 3. 数据入库 [t_command]
            commandService.announceFile(fileEntity.getId());
        }
        return fileEntity.getId();
    }

    @Override
    public FileEntity entity(long fileId) {
        return fileMapper.getById(fileId);
    }

}
