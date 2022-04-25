package io.shulie.takin.cloud.app.service.impl;

import javax.annotation.Resource;

import io.shulie.takin.cloud.app.mapper.CommandMapper;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;

/**
 * 命令服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class CommandServiceImpl implements CommandService {
@Resource
    CommandMapper commandMapper;
    /**
     * {@inheritDoc}
     */
    @Override
    public void graspResource(ResourceExampleEntity entity) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseResource(ResourceExampleEntity entity) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startApplication(Object obj) {

    }
}
