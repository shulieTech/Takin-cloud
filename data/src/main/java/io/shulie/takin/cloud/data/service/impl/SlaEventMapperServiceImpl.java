package io.shulie.takin.cloud.data.service.impl;

import io.shulie.takin.cloud.data.entity.SlaEventEntity;
import io.shulie.takin.cloud.data.mapper.SlaEventMapper;
import io.shulie.takin.cloud.data.service.SlaEventMapperService;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * Mapper - IService - Impl - SLA事件
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class SlaEventMapperServiceImpl
    extends ServiceImpl<SlaEventMapper, SlaEventEntity>
    implements SlaEventMapperService {
}
