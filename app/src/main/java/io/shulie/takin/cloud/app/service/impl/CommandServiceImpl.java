package io.shulie.takin.cloud.app.service.impl;

import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.shulie.takin.cloud.app.entity.JobEntity;
import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.app.entity.CommandEntity;
import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.app.service.mapper.CommandMapperService;

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
    @Resource
    JsonService jsonService;
    @Lazy
    @Resource
    ResourceService resourceService;
    @Resource
    CommandMapperService commandMapperService;

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
            setContext(jsonService.writeValueAsString(context));
        }};
        commandMapperService.save(commandEntity);
        return commandEntity.getId();
    }

    /**
     * 命令确认
     *
     * @param id      命令主键
     * @param context ack内容
     */
    public boolean ack(long id, String context) {
        return commandMapperService.lambdaUpdate()
            .set(CommandEntity::getAckContext, context)
            .eq(CommandEntity::getId, id)
            .update();
    }
}
