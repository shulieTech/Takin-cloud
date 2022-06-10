package io.shulie.takin.cloud.app.service.impl;

import java.util.Map;
import java.util.Objects;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.app.mapper.ResourceMapper;
import io.shulie.takin.cloud.app.entity.ResourceEntity;
import io.shulie.takin.cloud.app.entity.JobExampleEntity;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.constant.enums.NotifyEventType;
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
    ResourceMapper resourceMapper;
    @javax.annotation.Resource
    CallbackService callbackService;
    @javax.annotation.Resource
    ResourceExampleMapper resourceExampleMapper;
    @javax.annotation.Resource
    JobExampleMapperService jobExampleMapperService;
    @javax.annotation.Resource
    ResourceExampleEventMapper resourceExampleEventMapper;

    @Override
    public void onHeartbeat(long id) {
        // 基础信息准备
        StringBuilder callbackUrl = new StringBuilder();
        ResourceExampleHeartbeat context = new ResourceExampleHeartbeat();
        context.setData(getCallbackData(id, callbackUrl));
        // 创建回调
        callbackService.create(callbackUrl.toString(), jsonService.writeValueAsString(context));
        // 记录事件
        resourceExampleEventMapper.insert(new ResourceExampleEventEntity()
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
        boolean complete = callbackService.callback(null, callbackUrl.toString(), jsonService.writeValueAsString(context));
        log.info("锁定资源：{}, 回调结果: {}", id, complete);
        // 记录事件
        resourceExampleEventMapper.insert(new ResourceExampleEventEntity()
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
        boolean complete = callbackService.callback(null, callbackUrl.toString(), jsonService.writeValueAsString(context));
        log.info("释放资源：{}, 回调结果: {}", id, complete);
        // 记录事件
        resourceExampleEventMapper.insert(new ResourceExampleEventEntity()
                .setContext("{}")
                .setResourceExampleId(id)
                .setType(NotifyEventType.RESOUECE_EXAMPLE_STOP.getCode()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onInfo(long id, Map<String, Object> info) {
        // 提取错误信息
        final String errorFlag = "error";
        String errorMessage = info.getOrDefault(errorFlag, "").toString();
        info.remove(errorFlag);
        // 获取资源实例
        ResourceExampleEntity resourceExampleEntity = resourceExampleMapper.selectById(id);
        if (Objects.isNull(resourceExampleEntity)) {
            log.info("上报异常信息异常，资源实例ID[{}]对应的数据不存在:", id);
            return;
        }
        resourceExampleEventMapper.insert(new ResourceExampleEventEntity()
                .setResourceExampleId(id)
                .setType(NotifyEventType.RESOUECE_EXAMPLE_INFO.getCode())
                .setContext(jsonService.writeValueAsString(info)));

        if (CharSequenceUtil.isNotBlank(errorMessage)) {
            onError(id, errorMessage);
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
        boolean complete = callbackService.callback(null, callbackUrl.toString(), jsonService.writeValueAsString(context));
        log.info("资源异常信息：{}, 回调结果: {}", id, complete);
        // 记录事件
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("message", errorMessage);
        resourceExampleEventMapper.insert(new ResourceExampleEventEntity()
                .setResourceExampleId(id)
                .setContext(objectNode.toPrettyString())
                .setType(NotifyEventType.RESOUECE_EXAMPLE_ERROR.getCode()));
    }

    private ResourceExample getCallbackData(long resourceExampleId, StringBuilder callbackUrl) {
        // 获取资源实例
        ResourceExampleEntity resourceExampleEntity = resourceExampleMapper.selectById(resourceExampleId);
        // 获取资源
        ResourceEntity resourceEntity = resourceMapper.selectById(resourceExampleEntity.getResourceId());
        // 根据资源实例主键，获取任务实例主键
        JobExampleEntity jobExampleEntity = jobExampleMapperService.lambdaQuery()
                .eq(JobExampleEntity::getResourceExampleId, resourceExampleId).one();
        callbackUrl.append(resourceEntity.getCallbackUrl());
        return new ResourceExample()
                .setResourceExampleId(resourceExampleEntity.getId())
                .setResourceId(resourceExampleEntity.getResourceId())
                .setJobId(jobExampleEntity == null ? null : jobExampleEntity.getJobId())
                .setJobExampleId(jobExampleEntity == null ? null : jobExampleEntity.getId());
    }
}
