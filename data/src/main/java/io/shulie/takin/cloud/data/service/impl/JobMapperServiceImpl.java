package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.JobEntity;
import io.shulie.takin.cloud.data.mapper.JobMapper;
import io.shulie.takin.cloud.data.service.JobMapperService;

/**
 * Mapper - IService - Impl - 任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class JobMapperServiceImpl
    extends ServiceImpl<JobMapper, JobEntity>
    implements JobMapperService {
}
