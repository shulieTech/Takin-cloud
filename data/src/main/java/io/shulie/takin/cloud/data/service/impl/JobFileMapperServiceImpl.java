package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.JobFileEntity;
import io.shulie.takin.cloud.data.mapper.JobFileMapper;
import io.shulie.takin.cloud.data.service.JobFileMapperService;

/**
 * Mapper - IService - Impl - 任务文件
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class JobFileMapperServiceImpl extends ServiceImpl<JobFileMapper, JobFileEntity> implements JobFileMapperService {
}
