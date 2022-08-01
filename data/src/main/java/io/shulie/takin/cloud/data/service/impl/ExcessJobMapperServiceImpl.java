package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.mapper.ExcessJobMapper;
import io.shulie.takin.cloud.data.entity.ExcessJobEntity;
import io.shulie.takin.cloud.data.service.ExcessJobMapperService;

/**
 * Mapper - IService - Impl - 额外的任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ExcessJobMapperServiceImpl
    extends ServiceImpl<ExcessJobMapper, ExcessJobEntity>
    implements ExcessJobMapperService {
}
