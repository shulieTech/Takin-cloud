package io.shulie.takin.cloud.app.service.impl;

import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.shulie.takin.cloud.model.callback.*;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.data.entity.ResourceEntity;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.constant.enums.NotifyEventType;
import io.shulie.takin.cloud.constant.enums.BusinessStateEnum;
import io.shulie.takin.cloud.data.entity.PressureExampleEntity;
import io.shulie.takin.cloud.data.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.app.service.ResourceExampleService;
import io.shulie.takin.cloud.model.callback.basic.ResourceExample;
import io.shulie.takin.cloud.data.entity.ResourceExampleEventEntity;
import io.shulie.takin.cloud.data.service.PressureExampleMapperService;
import io.shulie.takin.cloud.data.service.ResourceExampleMapperService;
import io.shulie.takin.cloud.data.service.ResourceExampleEventMapperService;
import io.shulie.takin.cloud.model.request.job.resource.ResourceExampleInfoRequest;
import io.shulie.takin.cloud.model.callback.ResourceExampleError.ResourceExampleErrorInfo;

/**
 * 资源实例服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
@Slf4j
public class ResourceExampleServiceImpl implements ResourceExampleService {

    @javax.annotation.Resource
    JsonService jsonService;
    @javax.annotation.Resource
    CallbackService callbackService;
    @javax.annotation.Resource
    ResourceService resourceService;
    @javax.annotation.Resource(name = "resourceExampleMapperServiceImpl")
    ResourceExampleMapperService resourceExampleMapper;
    @javax.annotation.Resource(name = "pressureExampleMapperServiceImpl")
    PressureExampleMapperService pressureExampleMapper;
    @javax.annotation.Resource(name = "resourceExampleEventMapperServiceImpl")
    ResourceExampleEventMapperService resourceExampleEventMapper;

    @Override
    public void onHeartbeat(long id) {
        // 基础信息准备
        StringBuilder callbackUrl = new StringBuilder();
        ResourceExampleHeartbeat context = new ResourceExampleHeartbeat();
        context.setData(getCallbackData(id, callbackUrl));
        // 创建回调
        callbackService.create(callbackUrl.toString(), jsonService.writeValueAsString(context));
        // 记录事件
        resourceExampleEventMapper.save(new ResourceExampleEventEntity()
            .setContext("{}")
            .setResourceExampleId(id)
            .setType(NotifyEventType.RESOUECE_EXAMPLE_HEARTBEAT.getCode()));
    }

    @Override
    public void onStart(long id) {
        // 基础信息准备
        StringBuilder callbackUrl = new StringBuilder();
        ResourceExampleStart context = new ResourceExampleStart();
        context.setData(getCallbackData(id, callbackUrl));
        // 创建回调
        callbackService.create(callbackUrl.toString(), jsonService.writeValueAsString(context));
        log.info("锁定资源：{}", id);
        // 记录事件
        resourceExampleEventMapper.save(new ResourceExampleEventEntity()
            .setContext("{}")
            .setResourceExampleId(id)
            .setType(NotifyEventType.RESOUECE_EXAMPLE_START.getCode())
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop(long id) {
        // 基础信息准备
        StringBuilder callbackUrl = new StringBuilder();
        ResourceExampleStop context = new ResourceExampleStop();
        context.setData(getCallbackData(id, callbackUrl));
        // 创建回调
        callbackService.create(callbackUrl.toString(), jsonService.writeValueAsString(context));
        log.info("释放资源：{}", id);
        // 记录事件
        resourceExampleEventMapper.save(new ResourceExampleEventEntity()
            .setContext("{}")
            .setResourceExampleId(id)
            .setType(NotifyEventType.RESOUECE_EXAMPLE_STOP.getCode()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onInfo(long id, ResourceExampleInfoRequest info) {
        // 提取错误信息
        String errorMessage = info.getError();
        // 获取资源实例
        ResourceExampleEntity resourceExampleEntity = resourceExampleMapper.getById(id);
        if (Objects.isNull(resourceExampleEntity)) {
            log.info("上报异常信息异常，资源实例ID[{}]对应的数据不存在:", id);
            return;
        }
        resourceExampleEventMapper.save(new ResourceExampleEventEntity()
            .setResourceExampleId(id)
            .setType(NotifyEventType.RESOUECE_EXAMPLE_INFO.getCode())
            .setContext(jsonService.writeValueAsString(info)));

        if (Objects.equals(info.getBusinessState(), BusinessStateEnum.SUCCESSFUL.getState())) {
            //主动中断
            onSuccessful(id);
        } else if (CharSequenceUtil.isNotBlank(errorMessage)) {
            //错误信息
            onError(id, errorMessage);
        }
    }

    public void onSuccessful(long id) {
        // 基础信息准备
        StringBuilder callbackUrl = new StringBuilder();
        ResourceExampleSuccessful context = new ResourceExampleSuccessful();
        context.setData(getCallbackData(id, callbackUrl));

        boolean stoped = resourceExampleEventMapper.lambdaQuery()
            .eq(ResourceExampleEventEntity::getType, NotifyEventType.RESOUECE_EXAMPLE_SUCCESSFUL)
            .eq(ResourceExampleEventEntity::getResourceExampleId, id)
            .exists();
        if (!stoped) {
            // 创建回调
            callbackService.create(callbackUrl.toString(), jsonService.writeValueAsString(context));
            log.info("任务正常停止信息：{}", id);
            // 记录事件
            resourceExampleEventMapper.save(new ResourceExampleEventEntity()
                .setContext("{}")
                .setResourceExampleId(id)
                .setType(NotifyEventType.RESOUECE_EXAMPLE_SUCCESSFUL.getCode()));
        }
    }

    @Override
    public void onError(long id, String errorMessage) {
        // 基础信息准备
        StringBuilder callbackUrl = new StringBuilder();
        ResourceExample resourceExample = getCallbackData(id, callbackUrl);
        ResourceExampleErrorInfo errorInfo = new ResourceExampleErrorInfo(resourceExample);
        errorInfo.setErrorMessage(errorMessage);
        ResourceExampleError context = new ResourceExampleError();
        context.setData(errorInfo);
        // 创建回调
        callbackService.create(callbackUrl.toString(), jsonService.writeValueAsString(context));
        log.info("资源异常信息：{}", id);
        // 记录事件
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("message", errorMessage);
        resourceExampleEventMapper.save(new ResourceExampleEventEntity()
            .setResourceExampleId(id)
            .setContext(objectNode.toPrettyString())
            .setType(NotifyEventType.RESOUECE_EXAMPLE_ERROR.getCode()));
    }

    private ResourceExample getCallbackData(long resourceExampleId, StringBuilder callbackUrl) {
        // 获取资源实例
        ResourceExampleEntity resourceExampleEntity = resourceExampleMapper.getById(resourceExampleId);
        // 获取资源
        ResourceEntity resourceEntity = resourceService.entity(resourceExampleEntity.getResourceId());
        // 根据资源实例主键，获取任务实例主键
        PressureExampleEntity pressureExampleEntity = pressureExampleMapper.lambdaQuery()
            .eq(PressureExampleEntity::getResourceExampleId, resourceExampleId).one();
        callbackUrl.append(resourceEntity.getCallbackUrl());
        return new ResourceExample()
            .setResourceExampleId(resourceExampleEntity.getId())
            .setResourceId(resourceExampleEntity.getResourceId())
            .setPressureId(pressureExampleEntity == null ? null : pressureExampleEntity.getPressureId())
            .setPressureExampleId(pressureExampleEntity == null ? null : pressureExampleEntity.getId());
    }
}
