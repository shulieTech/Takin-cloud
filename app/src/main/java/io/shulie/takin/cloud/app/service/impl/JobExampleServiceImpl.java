package io.shulie.takin.cloud.app.service.impl;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.shulie.takin.cloud.app.entity.JobEntity;
import io.shulie.takin.cloud.app.mapper.JobMapper;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.constant.enums.NotifyEventType;
import io.shulie.takin.cloud.app.entity.JobExampleEntity;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.app.service.JobExampleService;
import io.shulie.takin.cloud.model.callback.JobExampleStop;
import io.shulie.takin.cloud.model.callback.JobExampleError;
import io.shulie.takin.cloud.model.callback.JobExampleStart;
import io.shulie.takin.cloud.model.callback.basic.JobExample;
import io.shulie.takin.cloud.app.entity.JobExampleEventEntity;
import io.shulie.takin.cloud.app.mapper.JobExampleEventMapper;
import io.shulie.takin.cloud.model.callback.JobExampleHeartbeat;
import io.shulie.takin.cloud.app.service.mapper.JobExampleMapperService;

/**
 * 任务实例服务 - 实现
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class JobExampleServiceImpl implements JobExampleService {
    @javax.annotation.Resource
    JobMapper jobMapper;
    @javax.annotation.Resource
    JsonService jsonService;
    @javax.annotation.Resource
    CallbackService callbackService;
    @javax.annotation.Resource
    JobExampleEventMapper jobExampleEventMapper;
    @javax.annotation.Resource
    JobExampleMapperService jobExampleMapperService;

    @Override
    public void onHeartbeat(long id) {
        // 基础信息准备
        StringBuffer callbackUrl = new StringBuffer();
        JobExampleHeartbeat context = new JobExampleHeartbeat() {{setData(getCallbackData(id, callbackUrl));}};
        // 创建回调
        callbackService.create(callbackUrl.toString(),
            jsonService.writeValueAsString(context).getBytes(StandardCharsets.UTF_8));
        // 记录事件
        jobExampleEventMapper.insert(new JobExampleEventEntity() {{
            setContext("{}");
            setType(NotifyEventType.JOB_EXAMPLE_HEARTBEAT.getCode());
            setJobExampleId(id);
        }});
    }

    @Override
    public void onStart(long id) {
        // 基础信息准备
        StringBuffer callbackUrl = new StringBuffer();
        JobExampleStart context = new JobExampleStart() {{setData(getCallbackData(id, callbackUrl));}};
        // 创建回调
        callbackService.create(callbackUrl.toString(),
            jsonService.writeValueAsString(context).getBytes(StandardCharsets.UTF_8));
        // 记录事件
        jobExampleEventMapper.insert(new JobExampleEventEntity() {{
            setContext("{}");
            setJobExampleId(id);
            setType(NotifyEventType.JOB_EXAMPLE_START.getCode());
        }});
    }

    @Override
    public void onStop(long id) {
        // 基础信息准备
        StringBuffer callbackUrl = new StringBuffer();
        JobExampleStop context = new JobExampleStop() {{setData(getCallbackData(id, callbackUrl));}};
        // 创建回调
        callbackService.create(callbackUrl.toString(),
            jsonService.writeValueAsString(context).getBytes(StandardCharsets.UTF_8));
        // 记录事件
        jobExampleEventMapper.insert(new JobExampleEventEntity() {{
            setContext("{}");
            setJobExampleId(id);
            setType(NotifyEventType.JOB_EXAMPLE_STOP.getCode());
        }});
    }

    @Override
    public void onError(long id, String errorMessage) {
        // 基础信息准备
        StringBuffer callbackUrl = new StringBuffer();
        JobExample jobExample = getCallbackData(id, callbackUrl);
        JobExampleError context = new JobExampleError() {{
            setData(new JobExampleErrorInfo(jobExample) {{setErrorMessage(errorMessage);}});
        }};
        // 创建回调
        callbackService.create(callbackUrl.toString(),
            jsonService.writeValueAsString(context).getBytes(StandardCharsets.UTF_8));
        // 记录事件
        jobExampleEventMapper.insert(new JobExampleEventEntity() {{
            ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
            objectNode.put("message", errorMessage);
            setJobExampleId(id);
            setContext(objectNode.toPrettyString());
            setType(NotifyEventType.JOB_EXAMPLE_ERROR.getCode());
        }});
    }
    @Override
    public JobExample getCallbackData(long jobExampleId, StringBuffer callbackUrl) {
        JobExampleEntity jobExampleEntity = jobExampleMapperService.getById(jobExampleId);
        JobEntity jobEntity = jobMapper.selectById(jobExampleEntity.getJobId());
        callbackUrl.append(jobEntity.getCallbackUrl());
        return new JobExample() {{
            setJobId(jobEntity.getId());
            setResourceId(jobEntity.getResourceId());
            setJobExampleId(jobExampleEntity.getId());
            setResourceExampleId(jobExampleEntity.getResourceExampleId());
        }};
    }
}
