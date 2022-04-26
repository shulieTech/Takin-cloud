package io.shulie.takin.cloud.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.github.pagehelper.PageInfo;
import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.app.entity.Watchman;
import io.shulie.takin.cloud.app.entity.WatchmanEvent;
import io.shulie.takin.cloud.app.mapper.WatchmanMapper;
import io.shulie.takin.cloud.app.model.resource.Resource;
import io.shulie.takin.cloud.app.service.WatchmanService;
import io.shulie.takin.cloud.app.mapper.WatchmanEventMapper;
import io.shulie.takin.cloud.app.entity.ResourceExampleEvent;
import io.shulie.takin.cloud.app.mapper.ResourceExampleEventMapper;

import io.shulie.takin.cloud.constant.enums.WatchmanEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 调度服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@Service
public class WatchmanServiceImpl implements WatchmanService {
    @javax.annotation.Resource
    WatchmanMapper watchmanMapper;
    @javax.annotation.Resource
    WatchmanEventMapper watchmanEventMapper;
    @javax.annotation.Resource
    ResourceExampleEventMapper resourceExampleEventMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * {@inheritDoc}
     */
    @Override
    public PageInfo<Watchman> list(int pageNumber, int pageSize) {
        try (Page<?> pageHelper = PageHelper.startPage(pageNumber, pageSize)) {
            return new PageInfo<Watchman>(watchmanMapper.selectList(null));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Resource> getResourceList(Long watchmanId) throws JsonProcessingException {
        List<Resource> result = new ArrayList<>(0);
        // 找到最后一次上报的数据
        try (Page<Resource> pageHelper = PageHelper.startPage(1, 1)) {
            // 查询条件 - 资源类型的上报
            Wrapper<WatchmanEvent> wrapper = new LambdaQueryWrapper<WatchmanEvent>()
                .orderByDesc(WatchmanEvent::getTime)
                .eq(WatchmanEvent::getType, WatchmanEventType.RESOURCE.getCode())
                .eq(WatchmanEvent::getWatchmanId, watchmanId);
            // 执行SQL
            PageInfo<WatchmanEvent> watchmanEventList = new PageInfo<>(watchmanEventMapper.selectList(wrapper));
            // 组装数据
            if (watchmanEventList.getList().size() > 0) {
                // 组装返回数据
                String eventContextString = watchmanEventList.getList().get(0).getContext();
                HashMap<String, String> eventContext = objectMapper.readValue(eventContextString, new TypeReference<HashMap<String, String>>() {});
                {
                    long resourceTime = Long.parseLong(String.valueOf(eventContext.get("time")));
                    // TODO 要校验时效
                    if (resourceTime < 0) {
                        log.warn("调度资源获取:最后一次上报的资源时效了");
                    }
                    String resourceListString = eventContext.get("data");
                    List<Resource> resourceList = objectMapper.readValue(resourceListString, new TypeReference<List<Resource>>() {});
                    // 处理数据
                    result.addAll(resourceList);
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("调度资源获取:JSON解析失败");
            throw e;
        }
        return result;
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
}
