package io.shulie.takin.cloud.app.service.impl;

import io.shulie.takin.cloud.app.service.JobService;
import org.springframework.stereotype.Service;

/**
 * 任务服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class JobServiceImpl implements JobService {
    /**
     * {@inheritDoc}
     */
    @Override
    public String start(Object jobInfo) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(long taskId) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getConfig(long taskId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyConfig(long taskId, Object context) {

    }
}
