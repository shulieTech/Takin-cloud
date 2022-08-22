package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.PressureFileEntity;
import io.shulie.takin.cloud.data.mapper.PressureFileMapper;
import io.shulie.takin.cloud.data.service.PressureFileMapperService;

/**
 * Mapper - IService - Impl - 施压任务文件
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class PressureFileMapperServiceImpl
    extends ServiceImpl<PressureFileMapper, PressureFileEntity>
    implements PressureFileMapperService {
}
