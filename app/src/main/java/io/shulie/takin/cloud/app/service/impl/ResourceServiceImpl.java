package io.shulie.takin.cloud.app.service.impl;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.Page;
import cn.hutool.core.text.CharSequenceUtil;
import com.github.pagehelper.page.PageMethod;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import com.fasterxml.jackson.core.type.TypeReference;

import io.shulie.takin.cloud.constant.Message;
import io.shulie.takin.cloud.data.entity.JobEntity;
import io.shulie.takin.cloud.app.util.ResourceUtil;
import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.model.resource.Resource;
import io.shulie.takin.cloud.data.entity.ResourceEntity;
import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.app.service.WatchmanService;
import io.shulie.takin.cloud.constant.enums.NotifyEventType;
import io.shulie.takin.cloud.data.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.data.service.ResourceMapperService;
import io.shulie.takin.cloud.model.request.ApplyResourceRequest;
import io.shulie.takin.cloud.constant.enums.ResourceExampleStatus;
import io.shulie.takin.cloud.data.entity.ResourceExampleEventEntity;
import io.shulie.takin.cloud.model.resource.ResourceExampleOverview;
import io.shulie.takin.cloud.data.service.ResourceExampleMapperService;
import io.shulie.takin.cloud.data.service.ResourceExampleEventMapperService;

/**
 * 资源服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {
    @Lazy
    @javax.annotation.Resource
    JobService jobService;
    @javax.annotation.Resource
    JsonService jsonService;
    @javax.annotation.Resource
    CommandService commandService;
    @javax.annotation.Resource
    WatchmanService watchmanService;
    @javax.annotation.Resource(name = "resourceMapperServiceImpl")
    ResourceMapperService resourceMapper;
    @javax.annotation.Resource(name = "resourceExampleMapperServiceImpl")
    ResourceExampleMapperService resourceExampleMapper;
    @javax.annotation.Resource(name = "resourceExampleEventMapperServiceImpl")
    ResourceExampleEventMapperService resourceExampleEventMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceExampleEntity> listExample(Long resourceId, Long jobId) {
        if (resourceId == null && jobId != null) {
            JobEntity jobEntity = jobService.jobEntity(jobId);
            resourceId = jobEntity == null ? null : jobEntity.getResourceId();
        }
        if (resourceId == null) {return new ArrayList<>(0);}
        // 查询条件
        return resourceExampleMapper.lambdaQuery()
            .eq(ResourceExampleEntity::getResourceId, resourceId)
            // 执行查询
            .list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean check(ApplyResourceRequest apply) {
        // 1. 声明需要的资源
        int number = apply.getNumber();
        Double requestCpu = ResourceUtil.convertCpu(apply.getCpu());
        Long requestMemory = ResourceUtil.convertMemory(apply.getMemory());
        if (requestCpu == null || requestMemory == null) {
            log.warn("请求的资源值无法解析.({},{})", apply.getCpu(), apply.getMemory());
            return false;
        }
        // 2. 获取调度所属的资源列表
        List<Resource> resourceList = watchmanService.getResourceList(apply.getWatchmanId());
        // 3. 循环判断每一个资源
        for (int i = 0; i < resourceList.size() && number > 0; i++) {
            Resource resource = resourceList.get(i);
            if (resource != null) {
                // 4. 声明每个资源拥有的量化资源
                Double spareCpu = ResourceUtil.convertCpu(resource.getCpu().toString());
                Long spareMemory = ResourceUtil.convertMemory(resource.getMemory().toString());
                // 空值校验
                if (spareCpu != null && spareMemory != null) {
                    // 5. 递减资源余量和申请的数量
                    while (number > 0 && spareCpu >= requestCpu && spareMemory >= requestMemory) {
                        number--;
                        spareCpu -= requestCpu;
                        spareMemory -= requestMemory;
                    }
                }
            }
        }
        // 6. 所需的资源数量是否全部满足了
        return number == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String lock(ApplyResourceRequest apply) {
        // 0. 预检
        if (this.check(apply)) {
            // 1. 保存任务信息
            ResourceEntity resourceEntity = new ResourceEntity()
                .setCpu(apply.getCpu())
                .setImage(apply.getImage())
                .setMemory(apply.getMemory())
                .setNumber(apply.getNumber())
                .setWatchmanId(apply.getWatchmanId())
                .setCallbackUrl(apply.getCallbackUrl())
                .setLimitCpu(CharSequenceUtil.isBlank(apply.getLimitCpu()) ? apply.getCpu() : apply.getLimitCpu())
                .setLimitMemory(CharSequenceUtil.isBlank(apply.getLimitMemory()) ? apply.getMemory() : apply.getLimitMemory());
            resourceMapper.save(resourceEntity);
            // 2. 创建任务实例
            for (int i = 0; i < apply.getNumber(); i++) {
                ResourceExampleEntity resourceExampleEntity = new ResourceExampleEntity()
                    .setCpu(apply.getCpu())
                    .setImage(apply.getImage())
                    .setMemory(apply.getMemory())
                    .setResourceId(resourceEntity.getId())
                    .setWatchmanId(resourceEntity.getWatchmanId())
                    .setLimitCpu(CharSequenceUtil.isBlank(apply.getLimitCpu()) ? apply.getCpu() : apply.getLimitCpu())
                    .setLimitMemory(CharSequenceUtil.isBlank(apply.getLimitMemory()) ? apply.getMemory() : apply.getLimitMemory());
                resourceExampleMapper.save(resourceExampleEntity);
            }
            // 3. 下发命令
            commandService.graspResource(resourceEntity.getId());
            // end 返回资源主键
            return resourceEntity.getId().toString();
        }
        // end 预检失败则直接返回 NULL
        else {return null;}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlock(long resourceId) {
        ResourceEntity resourceEntity = entity(resourceId);
        commandService.releaseResource(resourceEntity.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceExampleOverview exampleOverview(Long resourceExampleId) {
        ResourceExampleEntity resourceExampleEntity = resourceExampleMapper.getById(resourceExampleId);
        // 设置初始值
        ResourceExampleOverview result = new ResourceExampleOverview()
            .setStatus(ResourceExampleStatus.PENDING)
            .setStartTime(resourceExampleEntity.getCreateTime().getTime());
        // 找到最后一次上报的状态数据(启动、停止、异常)
        ResourceExampleEventEntity status = lastExampleStatus(resourceExampleId);
        if (status != null) {
            String contextString = status.getContext();
            Integer type = status.getType();
            result.setStatusTime(status.getTime().getTime());
            if (NotifyEventType.RESOUECE_EXAMPLE_START.getCode().equals(type)) {
                result.setStatus(ResourceExampleStatus.STARTED);
            } else if (NotifyEventType.RESOUECE_EXAMPLE_STOP.getCode().equals(type) || NotifyEventType.RESOUECE_EXAMPLE_SUCCESSFUL.getCode().equals(type)) {
                result.setStatus(ResourceExampleStatus.STOPED);
            } else if (NotifyEventType.RESOUECE_EXAMPLE_ERROR.getCode().equals(type)) {
                result.setStatus(ResourceExampleStatus.ABNORMAL);
            }
            Map<String, Object> context = jsonService.readValue(contextString, new TypeReference<Map<String, Object>>() {});
            result.setStatusMessage(context.get(Message.MESSAGE_NAME) == null ? null : context.get(Message.MESSAGE_NAME).toString());
        }
        // 设置资源实例信息
        ResourceExampleEventEntity info = lastExampleInfo(resourceExampleId);
        if (info != null) {
            fillResourceExampleOverviewInfo(info, result);
        }
        // 设置心跳接口时间
        else {
            try (Page<Object> ignored = PageMethod.startPage(1, 1)) {
                ResourceExampleEventEntity dbResult = resourceExampleEventMapper.lambdaQuery()
                    .orderByDesc(ResourceExampleEventEntity::getTime)
                    .eq(ResourceExampleEventEntity::getType, NotifyEventType.RESOUECE_EXAMPLE_HEARTBEAT.getCode())
                    .eq(ResourceExampleEventEntity::getResourceExampleId, resourceExampleId)
                    .one();
                if (dbResult != null) {
                    result.setStatusTime(dbResult.getTime().getTime());
                }
            }
        }
        return result;
    }

    /**
     * 最后上报的的资源实例状态
     *
     * @param resourceExampleId 资源实例主键
     * @return 上报的数据
     */
    ResourceExampleEventEntity lastExampleStatus(long resourceExampleId) {
        try (Page<Object> ignored = PageMethod.startPage(1, 1)) {
            return resourceExampleEventMapper.lambdaQuery()
                // 查询条件 - 状态类型
                .orderByDesc(ResourceExampleEventEntity::getTime)
                .notIn(ResourceExampleEventEntity::getType,
                    NotifyEventType.RESOUECE_EXAMPLE_HEARTBEAT.getCode(), NotifyEventType.RESOUECE_EXAMPLE_INFO.getCode())
                .eq(ResourceExampleEventEntity::getResourceExampleId, resourceExampleId)
                // 执行SQL
                .one();
        }
    }

    /**
     * 最后上报的的资源实例信息
     *
     * @param resourceExampleId 资源实例主键
     * @return 上报的数据
     */
    ResourceExampleEventEntity lastExampleInfo(long resourceExampleId) {
        try (Page<Object> ignored = PageMethod.startPage(1, 1)) {
            return resourceExampleEventMapper.lambdaQuery()
                .eq(ResourceExampleEventEntity::getType, NotifyEventType.RESOUECE_EXAMPLE_INFO.getCode())
                .eq(ResourceExampleEventEntity::getResourceExampleId, resourceExampleId)
                .orderByDesc(ResourceExampleEventEntity::getTime)
                .one();
        }
    }

    /**
     * 填充资源实例的概览信息
     *
     * @param entity 资源实例的最后一次信息上报时间
     * @param result 概览信息
     */
    void fillResourceExampleOverviewInfo(ResourceExampleEventEntity entity, ResourceExampleOverview result) {
        String contextString = entity.getContext();
        Map<String, Object> context = jsonService.readValue(contextString, new TypeReference<Map<String, Object>>() {});
        result.setIp(context.get("ip") == null ? null : context.get("ip").toString());
        result.setName(context.get("name") == null ? null : context.get("name").toString());
        result.setHostIp(context.get("hostIp") == null ? null : context.get("hostIp").toString());
        //如果没有设置过时间 - 代替心跳时间
        if (result.getStartTime() == null) {
            result.setStatusTime(entity.getTime().getTime());
        }
    }

    /**
     * {@inheritDoc}
     */
    public ResourceEntity entity(long id) {
        return resourceMapper.getById(id);
    }

    /**
     * {@inheritDoc}
     */
    public ResourceExampleEntity exampleEntity(long id) {
        return resourceExampleMapper.getById(id);
    }
}
