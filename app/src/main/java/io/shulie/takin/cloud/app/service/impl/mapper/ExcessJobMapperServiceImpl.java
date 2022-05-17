package io.shulie.takin.cloud.app.service.impl.mapper;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.app.mapper.ExcessJobMapper;
import io.shulie.takin.cloud.app.entity.ExcessJobEntity;
import io.shulie.takin.cloud.app.service.mapper.ExcessJobMapperService;

/**
 * Mapper - IService - Impl - 定时任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ExcessJobMapperServiceImpl extends ServiceImpl<ExcessJobMapper, ExcessJobEntity> implements ExcessJobMapperService {
}
