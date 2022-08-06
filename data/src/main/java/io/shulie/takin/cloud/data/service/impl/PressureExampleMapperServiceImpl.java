package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.mapper.PressureExampleMapper;
import io.shulie.takin.cloud.data.entity.PressureExampleEntity;
import io.shulie.takin.cloud.data.service.PressureExampleMapperService;

/**
 * Mapper - IService - Impl - 施压任务实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class PressureExampleMapperServiceImpl
    extends ServiceImpl<PressureExampleMapper, PressureExampleEntity>
    implements PressureExampleMapperService {
}
