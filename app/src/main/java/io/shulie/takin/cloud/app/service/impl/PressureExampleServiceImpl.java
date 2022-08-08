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
    PressureMapperService pressureMapper;
    @javax.annotation.Resource(name = "pressureExampleMapperServiceImpl")
    PressureExampleMapperService pressureExampleMapper;
    @javax.annotation.Resource(name = "pressureExampleEventMapperServiceImpl")
    PressureExampleEventMapperService pressureExampleEventMapper;

    @Override
    public void onHeartbeat(long pressureExampleId) {
        // 基础信息准备
        StringBuilder callbackUrl = new StringBuilder();
        PressureExampleHeartbeat context = new PressureExampleHeartbeat();
        context.setData(getCallbackData(pressureExampleId, callbackUrl));
        // 创建回调
        callbackService.create(callbackUrl.toString(), jsonService.writeValueAsString(context));
        // 记录事件
        pressureExampleEventMapper.save(new PressureExampleEventEntity()
            .setContext("{}")
            .setPressureExampleId(pressureExampleId)
            .setType(NotifyEventType.PRESSURE_EXAMPLE_HEARTBEAT.getCode())
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart(long pressureExampleId) {
        // 基础信息准备
        StringBuilder callbackUrl = new StringBuilder();
        PressureExampleStart context = new PressureExampleStart();
        context.setData(getCallbackData(pressureExampleId, callbackUrl));
        //回调
        callbackService.create(callbackUrl.toString(), jsonService.writeValueAsString(context));
        log.info("启动任务：{}", pressureExampleId);
        // 记录事件
        pressureExampleEventMapper.save(new PressureExampleEventEntity()
            .setContext("{}")
            .setPressureExampleId(pressureExampleId)
            .setType(NotifyEventType.PRESSURE_EXAMPLE_START.getCode())
        );
        //启动后触发心跳 防止超时
        onHeartbeat(pressureExampleId);
    }

    @Override
    public void onStop(long pressureExampleId) {
        // 基础信息准备
        StringBuilder callbackUrl = new StringBuilder();
        PressureExampleStop context = new PressureExampleStop();
        context.setData(getCallbackData(pressureExampleId, callbackUrl));
        // 创建回调
        callbackService.create(callbackUrl.toString(), jsonService.writeValueAsString(context));
        log.info("停止任务：{}", pressureExampleId);
        // 记录事件
        pressureExampleEventMapper.save(new PressureExampleEventEntity()
            .setContext("{}")
            .setPressureExampleId(pressureExampleId)
            .setType(NotifyEventType.PRESSURE_EXAMPLE_STOP.getCode())
        );
    }

    @Override
    public void onError(long pressureExampleId, String errorMessage) {
        // 基础信息准备
        StringBuilder callbackUrl = new StringBuilder();
        PressureExample pressureExample = getCallbackData(pressureExampleId, callbackUrl);
        PressureExampleErrorInfo errorInfo = new PressureExampleErrorInfo(pressureExample);
        errorInfo.setErrorMessage(errorMessage);
        PressureExampleError context = new PressureExampleError();
        context.setData(errorInfo);
        // 创建回调
        callbackService.create(callbackUrl.toString(), jsonService.writeValueAsString(context));
        log.info("任务异常信息回调：{}", pressureExampleId);
        // 记录事件
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("message", errorMessage);
        pressureExampleEventMapper.save(new PressureExampleEventEntity()
            .setPressureExampleId(pressureExampleId)
            .setContext(objectNode.toPrettyString())
            .setType(NotifyEventType.PRESSURE_EXAMPLE_ERROR.getCode())
        );
    }

    @Override
    public PressureExample getCallbackData(long pressureExampleId, StringBuilder callbackUrl) {
        PressureExampleEntity pressureExampleEntity = pressureExampleMapper.getById(pressureExampleId);
        PressureEntity pressureEntity = pressureMapper.getById(pressureExampleEntity.getPressureId());
        callbackUrl.append(pressureEntity.getCallbackUrl());
        return new PressureExample()
            .setPressureId(pressureEntity.getId())
            .setResourceId(pressureEntity.getResourceId())
            .setPressureExampleId(pressureExampleEntity.getId())
            .setResourceExampleId(pressureExampleEntity.getResourceExampleId());
    }
}
