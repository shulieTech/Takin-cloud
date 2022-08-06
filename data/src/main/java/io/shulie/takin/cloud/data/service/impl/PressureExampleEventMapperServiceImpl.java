package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.PressureExampleEventEntity;
import io.shulie.takin.cloud.data.mapper.PressureExampleEventMapper;
import io.shulie.takin.cloud.data.service.PressureExampleEventMapperService;

/**
 * Mapper - IService - Impl - 施压任务实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class PressureExampleEventMapperServiceImpl
    extends ServiceImpl<PressureExampleEventMapper, PressureExampleEventEntity>
    implements PressureExampleEventMapperService {
}
