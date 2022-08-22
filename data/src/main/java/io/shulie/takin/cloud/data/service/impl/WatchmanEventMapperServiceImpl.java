package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.WatchmanEventEntity;
import io.shulie.takin.cloud.data.mapper.WatchmanEventMapper;
import io.shulie.takin.cloud.data.service.WatchmanEventMapperService;

/**
 * Mapper - IService - Impl - 调度器事件
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class WatchmanEventMapperServiceImpl
    extends ServiceImpl<WatchmanEventMapper, WatchmanEventEntity>
    implements WatchmanEventMapperService {
}
