package io.shulie.takin.cloud.app.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.constant.enums.EventType;
import io.shulie.takin.cloud.app.mapper.ResourceMapper;
import io.shulie.takin.cloud.app.entity.ResourceEntity;
import io.shulie.takin.cloud.app.entity.JobExampleEntity;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.app.mapper.ResourceExampleMapper;
import io.shulie.takin.cloud.app.service.ResourceExampleService;
import io.shulie.takin.cloud.model.callback.ResourceExampleStop;
import io.shulie.takin.cloud.model.callback.ResourceExampleError;
import io.shulie.takin.cloud.model.callback.ResourceExampleStart;
import io.shulie.takin.cloud.model.callback.basic.ResourceExample;
import io.shulie.takin.cloud.app.mapper.ResourceExampleEventMapper;
import io.shulie.takin.cloud.app.entity.ResourceExampleEventEntity;
import io.shulie.takin.cloud.model.callback.ResourceExampleHeartbeat;
import io.shulie.takin.cloud.app.service.mapper.JobExampleMapperService;

/**
 * 资源实例服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ResourceExampleServiceImpl implements ResourceExampleService {

    @javax.annotation.Resource
    ResourceMapper resourceMapper;
    @javax.annotation.Resource
    ResourceExampleMapper resourceExampleMapper;
    @javax.annotation.Resource
    JobExampleMapperService jobExampleMapperService;
    @javax.annotation.Resource
    ResourceExampleEventMapper resourceExampleEventMapper;
    @javax.annotation.Resource
    CallbackService callbackService;
    @javax.annotation.Resource
    JsonService jsonService;

    @Override
    public void onHeartbeat(long id) {
        // 基础信息准备
        StringBuffer callbackUrl = new StringBuffer();
        ResourceExampleHeartbeat context = new ResourceExampleHeartbeat() {{setData(getCallbackData(id, callbackUrl));}};
        // 创建回调
        callbackService.create(callbackUrl.toString(),
            jsonService.writeValueAsString(context).getBytes(StandardCharsets.UTF_8));
        // 记录事件
        resourceExampleEventMapper.insert(new ResourceExampleEventEntity() {{
            setContext("{}");
            setType(EventType.RESOUECE_EXAMPLE_HEARTBEAT.getCode());
            setResourceExampleId(id);
        }});
    }

    @Override
    public void onStart(long id) {
        // 基础信息准备
        StringBuffer callbackUrl = new StringBuffer();
        ResourceExampleStart context = new ResourceExampleStart() {{setData(getCallbackData(id, callbackUrl));}};
        // 创建回调
        callbackService.create(callbackUrl.toString(),
            jsonService.writeValueAsString(context).getBytes(StandardCharsets.UTF_8));
        // 记录事件
        resourceExampleEventMapper.insert(new ResourceExampleEventEntity() {{
            setContext("{}");
            setType(EventType.RESOUECE_EXAMPLE_START.getCode());
            setResourceExampleId(id);
        }});
    }

    @Override
    public void onStop(long id) {
        // 基础信息准备
        StringBuffer callbackUrl = new StringBuffer();
        ResourceExampleStop context = new ResourceExampleStop() {{setData(getCallbackData(id, callbackUrl));}};
        // 创建回调
        callbackService.create(callbackUrl.toString(),
            jsonService.writeValueAsString(context).getBytes(StandardCharsets.UTF_8));
        // 记录事件
        resourceExampleEventMapper.insert(new ResourceExampleEventEntity() {{
            setContext("{}");
            setType(EventType.RESOUECE_EXAMPLE_STOP.getCode());
            setResourceExampleId(id);
        }});
    }

    @Override
    public void onError(long id, String errorMessage) {
        // 基础信息准备
        StringBuffer callbackUrl = new StringBuffer();
        ResourceExample resourceExample = getCallbackData(id, callbackUrl);
        ResourceExampleError context = new ResourceExampleError() {{
            setData(new ResourceExampleErrorInfo(resourceExample) {{setErrorMessage(errorMessage);}});
        }};
        // 创建回调
        callbackService.create(callbackUrl.toString(),
            jsonService.writeValueAsString(context).getBytes(StandardCharsets.UTF_8));
        // 记录事件
        resourceExampleEventMapper.insert(new ResourceExampleEventEntity() {{
            ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
            objectNode.put("message", errorMessage);
            setResourceExampleId(id);
            setContext(objectNode.toPrettyString());
            setType(EventType.RESOUECE_EXAMPLE_ERROR.getCode());
        }});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onInfo(long id, HashMap<String, Object> info) {
        resourceExampleEventMapper.insert(new ResourceExampleEventEntity() {{
            setResourceExampleId(id);
            setType(EventType.RESOUECE_EXAMPLE_INFO.getCode());
            setContext(jsonService.writeValueAsString(info));
        }});
    }

    private ResourceExample getCallbackData(long resourceExampleId, StringBuffer callbackUrl) {
        // 获取资源实例
        ResourceExampleEntity resourceExampleEntity = resourceExampleMapper.selectById(resourceExampleId);
        // 获取资源
        ResourceEntity resourceEntity = resourceMapper.selectById(resourceExampleEntity.getResourceId());
        // 根据资源实例主键，获取任务实例主键
        JobExampleEntity jobExampleEntity = jobExampleMapperService.lambdaQuery()
            .eq(JobExampleEntity::getResourceExampleId, resourceExampleId).one();
        callbackUrl.append(resourceEntity.getCallbackUrl());
        return new ResourceExample() {{
            setJobId(jobExampleEntity == null ? null : jobExampleEntity.getJobId());
            setJobExampleId(jobExampleEntity == null ? null : jobExampleEntity.getId());
            setResourceId(resourceExampleEntity.getResourceId());
            setResourceExampleId(resourceExampleEntity.getId());
        }};
    }
}
