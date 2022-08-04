package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.FileEntity;
import io.shulie.takin.cloud.data.mapper.FileMapper;
import io.shulie.takin.cloud.data.service.FileMapperService;

/**
 * Mapper - IService - Impl - 文件
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class FileMapperServiceImpl
    extends ServiceImpl<FileMapper, FileEntity>
    implements FileMapperService {
}
