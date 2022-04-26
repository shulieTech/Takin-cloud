package io.shulie.takin.cloud.app.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.shulie.takin.cloud.app.service.mapper.JobExampleMapperService;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.app.entity.JobEntity;
import io.shulie.takin.cloud.app.mapper.JobMapper;
import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.app.entity.ResourceEntity;
import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.app.entity.JobExampleEntity;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.app.model.response.JobConfig;
import io.shulie.takin.cloud.app.model.request.StartRequest;
import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.app.entity.ThreadConfigExampleEntity;
import io.shulie.takin.cloud.app.model.request.StartRequest.ThreadConfigInfo;

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
    JobExampleMapperService jobExampleMapperService;
    @Resource
    ResourceService resourceService;
    @Resource
    JobConfigServiceImpl jobConfigService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * {@inheritDoc}
     */
    @Override
    public String start(StartRequest jobInfo) {
        ResourceEntity resourceEntity = resourceService.entity(jobInfo.getResourceId());
        List<ResourceExampleEntity> resourceExampleEntityList = resourceService.listExample(resourceEntity.getId());
        JobEntity jobEntity = new JobEntity() {{
            setResourceId(resourceEntity.getId());
            // 时长取最大值
            setDuration(jobInfo.getThreadConfig().stream()
                .map(ThreadConfigInfo::getDuration)
                .max(Comparator.naturalOrder())
                .orElse(0));
            setSimpling(jobInfo.getSampling());
            setMode(jobInfo.getType().getCode());
            setCallbackUrl(jobInfo.getCallbackUrl());
            setResourceExampleNumber(resourceEntity.getNumber());
        }};
        jobMapper.insert(jobEntity);
        // 填充job实例
        List<JobExampleEntity> jobExampleEntityList = new ArrayList<>(resourceEntity.getNumber());
        for (int i = 0; i < resourceEntity.getNumber(); i++) {
            int finalI = i;
            jobExampleEntityList.add(new JobExampleEntity() {{
                setJobId(jobEntity.getId());
                setDuration(jobEntity.getDuration());
                setResourceExampleId(resourceExampleEntityList.get(finalI).getId());
            }});
        }
        jobExampleMapperService.saveBatch(jobExampleEntityList);

        jobExampleEntityList.forEach(t -> commandService.startApplication(t.getId()));
        return jobEntity.getId() + "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(long jobId) {
        // 获取任务
        JobEntity jobEntity = jobMapper.selectById(jobId);
        // 获取任务实例
        List<JobExampleEntity> jobExampleEntityList = jobExampleMapperService.lambdaQuery()
            .eq(JobExampleEntity::getJobId, jobEntity.getId()).list();
        // 逐个停止
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
    public void modifyConfig(long jobId, JobConfig context) throws JsonProcessingException {
        // 1. 找到要修改的配置项
        ThreadConfigExampleEntity threadConfigExampleEntity = jobConfigService.threadExampleItem(jobId, context.getRef());
        String contextString=objectMapper.writeValueAsString(context.getContext());
        // 2. 如果没有则新增
        if (threadConfigExampleEntity == null) {
            ThreadConfigExampleEntity newThreadConfigExampleEntity = new ThreadConfigExampleEntity() {{
                setJobId(jobId);
                setRef(context.getRef());
                setMode(context.getMode());
                setContext(contextString);
            }};
            // 2.1 创建任务配置实例项
            jobConfigService.createThreadExample(newThreadConfigExampleEntity);
        }
        // 存在即修改
        else {
            // 2.1 更新任务配置实例项
            jobConfigService.modifThreadConfigExample(threadConfigExampleEntity.getId(), context.getMode(), contextString);
            // 2.2 下发命令
        }
        commandService.updateConfig(jobId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobEntity jobEntity(long jobId) {
        return jobMapper.selectById(jobId);
    }

}
