package io.shulie.takin.cloud.app.service.impl;

import java.util.List;
import java.util.HashMap;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.app.entity.MetricsEntity;
import io.shulie.takin.cloud.app.mapper.MetricsMapper;
import io.shulie.takin.cloud.app.service.JobConfigService;
import io.shulie.takin.cloud.app.mapper.ThreadConfigMapper;
import io.shulie.takin.cloud.app.entity.ThreadConfigEntity;
import io.shulie.takin.cloud.app.mapper.ThreadConfigExampleMapper;
import io.shulie.takin.cloud.app.entity.ThreadConfigExampleEntity;

/**
 * 任务配置服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@Service
public class JobConfigServiceImpl implements JobConfigService {
    @Resource
    MetricsMapper metricsMapper;
    @Resource
    ThreadConfigMapper threadConfigMapper;
    @Resource
    ThreadConfigExampleMapper threadConfigExampleMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MetricsEntity> metricsList(long jobId) {
        return metricsMapper.lambdaQuery()
            .eq(MetricsEntity::getJobId, jobId)
            .list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ThreadConfigExampleEntity> threadList(long jobId) {
        return threadConfigExampleMapper.lambdaQuery()
            .eq(ThreadConfigExampleEntity::getJobId, jobId)
            .list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ThreadConfigExampleEntity threadExampleItem(long jobId, String ref) {
        return threadConfigExampleMapper.lambdaQuery()
            .eq(ThreadConfigExampleEntity::getJobId, jobId)
            .eq(ThreadConfigExampleEntity::getRef, ref)
            .one();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifThreadConfigExample(long threadConfigExampleId,Integer mode, HashMap<String, Object> context) {
        threadConfigExampleMapper.updateById(new ThreadConfigExampleEntity() {{
            setId(threadConfigExampleId);
            setMode(mode);
            setContext(getContext());
        }});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createMetrics(List<MetricsEntity> metricsEntityList) {
        metricsMapper.saveBatch(metricsEntityList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createThread(List<ThreadConfigEntity> metricsEntityList) {
        threadConfigMapper.saveBatch(metricsEntityList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createThreadExample(List<ThreadConfigExampleEntity> threadConfigExampleEntityList) {
        threadConfigExampleMapper.saveBatch(threadConfigExampleEntityList);
    }
}
