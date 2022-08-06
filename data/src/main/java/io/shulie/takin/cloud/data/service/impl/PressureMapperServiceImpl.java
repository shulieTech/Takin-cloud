package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.PressureEntity;
import io.shulie.takin.cloud.data.mapper.PressureMapper;
import io.shulie.takin.cloud.data.service.PressureMapperService;

/**
 * Mapper - IService - Impl - 施压任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class PressureMapperServiceImpl
    extends ServiceImpl<PressureMapper, PressureEntity>
    implements PressureMapperService {
}
