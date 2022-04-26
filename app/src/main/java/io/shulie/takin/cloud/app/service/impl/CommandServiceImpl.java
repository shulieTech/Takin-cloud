package io.shulie.takin.cloud.app.service.impl;

import java.util.HashMap;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import io.shulie.takin.cloud.app.entity.JobEntity;
import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.app.entity.CommandEntity;
import io.shulie.takin.cloud.app.mapper.CommandMapper;
import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;

/**
 * 命令服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
@SuppressWarnings("unused")
public class CommandServiceImpl implements CommandService {
    @Lazy
    @Resource
    JobService jobService;
    @Lazy
    @Resource
    ResourceService resourceService;
    @Resource
    CommandMapper commandMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        JobEntity jobEntity = jobService.jobEntity(jobId);
        // TODO 实现
    }

    /**
     * 命令入库
     *
     * @param type    命令类型
     * @param context 命令类容
     * @return 命令主键
     * @throws JsonProcessingException JSON异常
     */
    private long create(Integer type, HashMap<String, Object> context) throws JsonProcessingException {
        CommandEntity commandEntity = new CommandEntity() {{
            setType(type);
            setContext(objectMapper.writeValueAsString(context));
        }};
        commandMapper.insert(commandEntity);
        return commandEntity.getId();
    }
}
