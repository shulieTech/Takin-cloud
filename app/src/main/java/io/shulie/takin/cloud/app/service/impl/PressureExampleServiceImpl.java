package io.shulie.takin.cloud.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.data.entity.PressureEntity;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.constant.enums.NotifyEventType;
import io.shulie.takin.cloud.data.entity.PressureExampleEntity;
import io.shulie.takin.cloud.app.service.PressureExampleService;
import io.shulie.takin.cloud.model.callback.PressureExampleStop;
import io.shulie.takin.cloud.data.service.PressureMapperService;
import io.shulie.takin.cloud.model.callback.PressureExampleError;
import io.shulie.takin.cloud.model.callback.PressureExampleStart;
import io.shulie.takin.cloud.model.callback.basic.PressureExample;
import io.shulie.takin.cloud.data.entity.PressureExampleEventEntity;
import io.shulie.takin.cloud.model.callback.PressureExampleHeartbeat;
import io.shulie.takin.cloud.data.service.PressureExampleMapperService;
import io.shulie.takin.cloud.data.service.PressureExampleEventMapperService;
import io.shulie.takin.cloud.model.callback.PressureExampleError.PressureExampleErrorInfo;

/**
 * 施压任务实例服务 - 实现
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
@Slf4j
public class PressureExampleServiceImpl implements PressureExampleService {
    @javax.annotation.Resource
    JsonService jsonService;
    @javax.annotation.Resource
    CallbackService callbackService;
    @javax.annotation.Resource(name = "pressureMapperServiceImpl")
    PressureMapperService jobMapper;
    @javax.annotation.Resource(name = "pressureExampleMapperServiceImpl")
    PressureExampleMapperService jobExampleMapper;
    @javax.annotation.Resource(name = "pressureExampleEventMapperServiceImpl")
    PressureExampleEventMapperService jobExampleEventMapper;

    @Override
    public void onHeartbeat(long id) {
        // 基础信息准备
        StringBuilder callbackUrl = new StringBuilder();
        PressureExampleHeartbeat context = new PressureExampleHeartbeat();
        context.setData(getCallbackData(id, callbackUrl));
        // 创建回调
        callbackService.callback(null, callbackUrl.toString(), jsonService.writeValueAsString(context));
        // 记录事件
        jobExampleEventMapper.save(new PressureExampleEventEntity()
            .setContext("{}")
            .setJobExampleId(id)
            .setType(NotifyEventType.PRESSURE_EXAMPLE_HEARTBEAT.getCode())
        );
    }

    @Override
    public void onStart(long id) {
        // 基础信息准备
        StringBuilder callbackUrl = new StringBuilder();
        PressureExampleStart context = new PressureExampleStart();
        context.setData(getCallbackData(id, callbackUrl));
        //回调
        boolean complete = callbackService.callback(null, callbackUrl.toString(), jsonService.writeValueAsString(context));
        log.info("启动任务：{}, 回调结果: {}", id, complete);
        // 记录事件
        jobExampleEventMapper.save(new PressureExampleEventEntity()
            .setContext("{}")
            .setJobExampleId(id)
            .setType(NotifyEventType.PRESSURE_EXAMPLE_START.getCode())
        );
        //启动后触发心跳 防止超时
        onHeartbeat(id);
    }

    @Override
    public void onStop(long id) {
        // 基础信息准备
        StringBuilder callbackUrl = new StringBuilder();
        PressureExampleStop context = new PressureExampleStop();
        context.setData(getCallbackData(id, callbackUrl));
        // 创建回调
        boolean complete = callbackService.callback(null, callbackUrl.toString(), jsonService.writeValueAsString(context));
        log.info("停止任务：{}, 回调结果: {}", id, complete);
        // 记录事件
        jobExampleEventMapper.save(new PressureExampleEventEntity()
            .setContext("{}")
            .setJobExampleId(id)
            .setType(NotifyEventType.PRESSURE_EXAMPLE_STOP.getCode())
        );
    }

    @Override
    public void onError(long id, String errorMessage) {
        // 基础信息准备
        StringBuilder callbackUrl = new StringBuilder();
        PressureExample pressureExample = getCallbackData(id, callbackUrl);
        PressureExampleErrorInfo errorInfo = new PressureExampleErrorInfo(pressureExample);
        errorInfo.setErrorMessage(errorMessage);
        PressureExampleError context = new PressureExampleError();
        context.setData(errorInfo);
        // 创建回调
        boolean complete = callbackService.callback(null, callbackUrl.toString(), jsonService.writeValueAsString(context));
        log.info("任务异常信息回调：{}, 回调结果: {}", id, complete);
        // 记录事件
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("message", errorMessage);
        jobExampleEventMapper.save(new PressureExampleEventEntity()
            .setJobExampleId(id)
            .setContext(objectNode.toPrettyString())
            .setType(NotifyEventType.PRESSURE_EXAMPLE_ERROR.getCode())
        );
    }

    @Override
    public PressureExample getCallbackData(long jobExampleId, StringBuilder callbackUrl) {
        PressureExampleEntity pressureExampleEntity = jobExampleMapper.getById(jobExampleId);
        PressureEntity pressureEntity = jobMapper.getById(pressureExampleEntity.getJobId());
        callbackUrl.append(pressureEntity.getCallbackUrl());
        return new PressureExample()
            .setJobId(pressureEntity.getId())
            .setResourceId(pressureEntity.getResourceId())
            .setJobExampleId(pressureExampleEntity.getId())
            .setResourceExampleId(pressureExampleEntity.getResourceExampleId());
    }
}
