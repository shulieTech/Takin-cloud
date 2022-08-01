package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.ResourceExampleEventEntity;
import io.shulie.takin.cloud.data.mapper.ResourceExampleEventMapper;
import io.shulie.takin.cloud.data.service.ResourceExampleEventMapperService;

/**
 * Mapper - IService - Impl - 资源实例事件
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ResourceExampleEventMapperServiceImpl
    extends ServiceImpl<ResourceExampleEventMapper, ResourceExampleEventEntity>
    implements ResourceExampleEventMapperService {
}
