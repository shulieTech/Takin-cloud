package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.mapper.JobExampleMapper;
import io.shulie.takin.cloud.data.entity.JobExampleEntity;
import io.shulie.takin.cloud.data.service.JobExampleMapperService;

/**
 * Mapper - IService - Impl - 任务实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class JobExampleMapperServiceImpl
    extends ServiceImpl<JobExampleMapper, JobExampleEntity>
    implements JobExampleMapperService {
}
