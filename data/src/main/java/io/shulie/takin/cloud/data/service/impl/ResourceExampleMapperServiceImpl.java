package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.data.mapper.ResourceExampleMapper;
import io.shulie.takin.cloud.data.service.ResourceExampleMapperService;

/**
 * Mapper - IService - Impl - 资源实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ResourceExampleMapperServiceImpl
    extends ServiceImpl<ResourceExampleMapper, ResourceExampleEntity>
    implements ResourceExampleMapperService {
}
