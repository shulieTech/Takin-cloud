package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.JobExampleEventEntity;
import io.shulie.takin.cloud.data.mapper.JobExampleEventMapper;
import io.shulie.takin.cloud.data.service.JobExampleEventMapperService;

/**
 * Mapper - IService - Impl - 任务实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class JobExampleEventMapperServiceImpl
    extends ServiceImpl<JobExampleEventMapper, JobExampleEventEntity>
    implements JobExampleEventMapperService {
}
