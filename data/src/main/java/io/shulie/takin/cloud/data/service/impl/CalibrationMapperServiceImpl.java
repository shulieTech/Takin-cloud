package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.mapper.CalibrationMapper;
import io.shulie.takin.cloud.data.entity.CalibrationEntity;
import io.shulie.takin.cloud.data.service.CalibrationMapperService;

/**
 * Mapper - IService - Impl - 数据校准任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class CalibrationMapperServiceImpl
    extends ServiceImpl<CalibrationMapper, CalibrationEntity>
    implements CalibrationMapperService {
}
