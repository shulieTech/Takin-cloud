package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.CalibrationLogEntity;
import io.shulie.takin.cloud.data.mapper.CalibrationLogMapper;
import io.shulie.takin.cloud.data.service.CalibrationLogMapperService;

/**
 * Mapper - IService - Impl - 数据校准任务的记录
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class CalibrationLogMapperServiceImpl
    extends ServiceImpl<CalibrationLogMapper, CalibrationLogEntity>
    implements CalibrationLogMapperService {
}
