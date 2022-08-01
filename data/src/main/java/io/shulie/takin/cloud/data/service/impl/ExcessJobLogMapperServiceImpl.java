package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.ExcessJobLogEntity;
import io.shulie.takin.cloud.data.mapper.ExcessJobLogMapper;
import io.shulie.takin.cloud.data.service.ExcessJobLogMapperService;

/**
 * Mapper - IService - Impl - 定时任务记录
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ExcessJobLogMapperServiceImpl extends ServiceImpl<ExcessJobLogMapper, ExcessJobLogEntity> implements ExcessJobLogMapperService {
}
