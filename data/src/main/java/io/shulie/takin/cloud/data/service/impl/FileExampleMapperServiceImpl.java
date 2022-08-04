package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.FileExampleEntity;
import io.shulie.takin.cloud.data.mapper.FileExampleMapper;
import io.shulie.takin.cloud.data.service.FileExampleMapperService;

/**
 * Mapper - IService - Impl - 文件实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class FileExampleMapperServiceImpl
    extends ServiceImpl<FileExampleMapper, FileExampleEntity>
    implements FileExampleMapperService {
}
