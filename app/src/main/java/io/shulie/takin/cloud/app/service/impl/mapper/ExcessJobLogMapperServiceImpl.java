package io.shulie.takin.cloud.app.service.impl.mapper;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.app.entity.ExcessJobLogEntity;
import io.shulie.takin.cloud.app.mapper.ExcessJobLogMapper;
import io.shulie.takin.cloud.app.service.mapper.ExcessJobLogMapperService;

/**
 * Mapper - IService - Impl - 定时任务记录
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ExcessJobLogMapperServiceImpl extends ServiceImpl<ExcessJobLogMapper, ExcessJobLogEntity> implements ExcessJobLogMapperService {
}
