package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.ResourceEntity;
import io.shulie.takin.cloud.data.mapper.ResourceMapper;
import io.shulie.takin.cloud.data.service.ResourceMapperService;

/**
 * Mapper - IService - Impl - 资源
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ResourceMapperServiceImpl
    extends ServiceImpl<ResourceMapper, ResourceEntity>
    implements ResourceMapperService {
}
