package io.shulie.takin.cloud.app.service.impl;

import java.util.HashMap;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;
import io.shulie.takin.cloud.app.util.ResourceUtil;
import io.shulie.takin.cloud.app.mapper.ResourceMapper;
import io.shulie.takin.cloud.app.entity.ResourceEntity;
import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.app.model.resource.Resource;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.app.service.WatchmanService;
import io.shulie.takin.cloud.app.entity.ResourceExampleEvent;
import io.shulie.takin.cloud.app.mapper.ResourceExampleMapper;
import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.app.mapper.ResourceExampleEventMapper;
import io.shulie.takin.cloud.app.model.request.ApplyResourceRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 资源服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {

    @javax.annotation.Resource
    ResourceMapper resourceMapper;
    @javax.annotation.Resource
    CommandService commandService;
    @javax.annotation.Resource
    WatchmanService watchmanService;
    @javax.annotation.Resource
    ResourceExampleMapper resourceExampleMapper;
    @javax.annotation.Resource
    ResourceExampleEventMapper resourceExampleEventMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceExampleEntity> listExample(Long resourceId) {
        // 查询条件
        Wrapper<ResourceExampleEntity> wrapper = new LambdaQueryWrapper<ResourceExampleEntity>()
            .eq(ResourceExampleEntity::getResourceId, resourceId);
        // 执行查询
        return resourceExampleMapper.selectList(wrapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean check(ApplyResourceRequest apply) throws JsonProcessingException {
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
    public String lock(ApplyResourceRequest apply, String callbackUrl) throws JsonProcessingException {
        // 0. 预检
        if (this.check(apply)) {
            // 1. 保存任务信息
            ResourceEntity resourceEntity = new ResourceEntity() {{
                setCallbackUrl(callbackUrl);
                setNumber(apply.getNumber());
                setWatchmanId(apply.getWatchmanId());
                // 创建时间由数据库维护
                setCpu(apply.getCpu());
                setLimitCpu(StrUtil.isBlank(apply.getLimitCpu()) ? apply.getCpu() : apply.getLimitCpu());
                setMemory(apply.getMemory());
                setLimitMemory(StrUtil.isBlank(apply.getLimitMemory()) ? apply.getMemory() : apply.getLimitMemory());
            }};
            resourceMapper.insert(resourceEntity);
            // 2. 创建任务实例
            for (int i = 0; i < apply.getNumber(); i++) {
                ResourceExampleEntity resourceExampleEntity = new ResourceExampleEntity() {{
                    setResourceId(resourceEntity.getId());
                    setWatchmanId(resourceEntity.getWatchmanId());
                    // 创建时间由数据库维护
                    setCpu(apply.getCpu());
                    setLimitCpu(StrUtil.isBlank(apply.getLimitCpu()) ? apply.getCpu() : apply.getLimitCpu());
                    setMemory(apply.getMemory());
                    setLimitMemory(StrUtil.isBlank(apply.getLimitMemory()) ? apply.getMemory() : apply.getLimitMemory());
                }};
                resourceExampleMapper.insert(resourceExampleEntity);
                // 3. 下发命令
                commandService.graspResource(resourceExampleEntity.getId());
            }
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
    public Object exampleOverview(Long resourceExampleId) throws JsonProcessingException {
        // 找到最后一次上报的数据
        try (Page<Object> pageHelper = PageHelper.startPage(1, 1)) {
            // 查询条件 - 资源类型的上报
            Wrapper<ResourceExampleEvent> wrapper = new LambdaQueryWrapper<ResourceExampleEvent>()
                .orderByDesc(ResourceExampleEvent::getTime)
                .eq(ResourceExampleEvent::getType, "")
                .eq(ResourceExampleEvent::getResourceExampleId, resourceExampleId);
            // 执行SQL
            PageInfo<ResourceExampleEvent> watchmanEventList = new PageInfo<>(resourceExampleEventMapper.selectList(wrapper));
            if (watchmanEventList.getList().size() > 0) {
                // 组装返回数据
                // 组装返回数据
                String eventContextString = watchmanEventList.getList().get(0).getContext();
                HashMap<String, String> eventContext = objectMapper.readValue(eventContextString, new TypeReference<HashMap<String, String>>() {});
                return eventContext.get("data");
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ResourceEntity entity(long id) {
        return resourceMapper.selectById(id);
    }

    /**
     * {@inheritDoc}
     */
    public ResourceExampleEntity exampleEntity(long id) {
        return resourceExampleMapper.selectById(id);
    }
}
