package io.shulie.takin.cloud.app.service.impl.mapper;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.app.mapper.JobExampleMapper;
import io.shulie.takin.cloud.app.entity.JobExampleEntity;
import io.shulie.takin.cloud.app.service.mapper.JobExampleMapperService;

/**
 * Mapper - IService - Impl - 任务实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class JobExampleMapperServiceImpl extends ServiceImpl<JobExampleMapper, JobExampleEntity> implements JobExampleMapperService {
}
