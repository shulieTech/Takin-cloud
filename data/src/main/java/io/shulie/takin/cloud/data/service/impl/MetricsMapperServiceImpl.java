package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.data.entity.MetricsEntity;
import io.shulie.takin.cloud.data.mapper.MetricsMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.cloud.data.service.MetricsMapperService;

/**
 * Mapper - IService - Impl - 指标配置
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class MetricsMapperServiceImpl extends ServiceImpl<MetricsMapper, MetricsEntity> implements MetricsMapperService {
}
