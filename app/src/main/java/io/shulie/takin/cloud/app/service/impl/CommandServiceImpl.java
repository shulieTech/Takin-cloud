package io.shulie.takin.cloud.app.service.impl;

import javax.annotation.Resource;

import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.app.service.ResourceService;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.app.mapper.JobMapper;
import io.shulie.takin.cloud.app.mapper.CommandMapper;
import io.shulie.takin.cloud.app.mapper.ResourceMapper;
import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.app.mapper.JobExampleMapper;
import io.shulie.takin.cloud.app.mapper.ResourceExampleMapper;

/**
 * 命令服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class CommandServiceImpl implements CommandService {
    @Resource
    JobService jobService;
    @Resource
    CommandMapper commandMapper;
    @Resource
    ResourceService resourceService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void graspResource(long resourceExampleId) {
        ResourceExampleEntity resourceExampleEntity = resourceService.exampleEntity(resourceExampleId);
        // TODO 实现
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseResource(long resourceExampleId) {
        ResourceExampleEntity resourceExampleEntity = resourceService.exampleEntity(resourceExampleId);
        // TODO 实现
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startApplication(long jobExampleId) {
        // TODO 实现
    }

    @Override
    public void stopApplication(long jobExampleId) {
        // TODO 实现
    }

    @Override
    public void updateConfig(long jobId) {
        // TODO 实现
    }
}
