package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.mapper.SlaMapper;
import io.shulie.takin.cloud.data.entity.SlaEntity;
import io.shulie.takin.cloud.data.service.SlaMapperService;

/**
 * Mapper - IService - Impl - SLA
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class SlaMapperServiceImpl extends ServiceImpl<SlaMapper, SlaEntity> implements SlaMapperService {
}
