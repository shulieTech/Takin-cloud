package io.shulie.takin.cloud.app.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.shulie.takin.cloud.app.entity.JobEntity;
import io.shulie.takin.cloud.app.entity.JobExampleEntity;
import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.app.entity.ThreadConfigExampleEntity;
import io.shulie.takin.cloud.app.mapper.JobExampleMapper;
import io.shulie.takin.cloud.app.mapper.JobMapper;
import io.shulie.takin.cloud.app.model.response.JobConfig;
import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.app.service.ResourceService;
import org.springframework.stereotype.Service;

/**
 * 任务服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class JobServiceImpl implements JobService {
    @Resource
    JobMapper jobMapper;
    @Resource
    CommandService commandService;
    @Resource
    JobExampleMapper jobExampleMapper;
    @Resource
    ResourceService resourceService;
    @Resource
    JobConfigServiceImpl jobConfigService;

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
    public void stop(long jobId) {
        // 获取任务实例
        JobEntity jobEntity = jobMapper.selectById(jobId);
        //
        Wrapper<JobExampleEntity> wrapper = new LambdaQueryWrapper<JobExampleEntity>()
            .eq(JobExampleEntity::getJobId, jobEntity.getId());
        List<JobExampleEntity> jobExampleEntityList = jobExampleMapper.selectList(wrapper);
        jobExampleEntityList.forEach(t -> {
            ResourceExampleEntity exampleEntity = resourceService.exampleEntity(t.getResourceExampleId());
            commandService.stopApplication(t.getId());
            commandService.releaseResource(exampleEntity.getId());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ThreadConfigExampleEntity> getConfig(long taskId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyConfig(long jobId, JobConfig context) {
        long modifThreadConfigExampleId = jobId;
        // 1. 找到要修改的配置项
        ThreadConfigExampleEntity threadConfigExampleEntity = jobConfigService.threadExampleItem(modifThreadConfigExampleId, context.getRef());
        // 2. 如果没有则新增
        if (threadConfigExampleEntity == null) {
            ThreadConfigExampleEntity newThreadConfigExampleEntity = new ThreadConfigExampleEntity() {{
                setJobId(jobId);
                setRef(context.getRef());
                setMode(context.getMode());
                setContext(context.getContext());
            }};
            // 2.1 创建任务配置实例项
            jobConfigService.createThreadExample(newThreadConfigExampleEntity);
            modifThreadConfigExampleId = newThreadConfigExampleEntity.getId();
        } else {
            // 2.1 更新任务配置实例项
            jobConfigService.modifThreadConfigExample(modifThreadConfigExampleId, context.getMode(), context.getContext());
            // 2.2 下发命令
        }
        commandService.updateConfig(jobId);
    }

}
