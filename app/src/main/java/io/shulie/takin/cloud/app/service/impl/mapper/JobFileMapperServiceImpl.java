package io.shulie.takin.cloud.app.service.impl.mapper;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.app.entity.JobFileEntity;
import io.shulie.takin.cloud.app.mapper.JobFileMapper;
import io.shulie.takin.cloud.app.service.mapper.JobFileMapperService;

/**
 * Mapper - IService - Impl - 任务文件
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class JobFileMapperServiceImpl extends ServiceImpl<JobFileMapper, JobFileEntity> implements JobFileMapperService {
}
