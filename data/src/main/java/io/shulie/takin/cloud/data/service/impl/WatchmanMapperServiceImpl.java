package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.mapper.WatchmanMapper;
import io.shulie.takin.cloud.data.entity.WatchmanEntity;
import io.shulie.takin.cloud.data.service.WatchmanMapperService;

/**
 * Mapper - IService - Impl - 调度器
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class WatchmanMapperServiceImpl
    extends ServiceImpl<WatchmanMapper, WatchmanEntity>
    implements WatchmanMapperService {
}
