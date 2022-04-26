package io.shulie.takin.cloud.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.shulie.takin.cloud.app.service.mapper.WatchmanMapperService;
import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.app.entity.WatchmanEntity;
import io.shulie.takin.cloud.app.model.resource.Resource;
import io.shulie.takin.cloud.app.service.WatchmanService;
import io.shulie.takin.cloud.app.entity.WatchmanEventEntity;
import io.shulie.takin.cloud.app.mapper.WatchmanEventMapper;
import io.shulie.takin.cloud.constant.enums.WatchmanEventType;

/**
 * 调度服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@Service
public class WatchmanServiceImpl implements WatchmanService {
    @javax.annotation.Resource
    WatchmanEventMapper watchmanEventMapper;
    @javax.annotation.Resource
    WatchmanMapperService watchmanMapperService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * {@inheritDoc}
     */
    @Override
    public PageInfo<WatchmanEntity> list(int pageNumber, int pageSize) {
        try (Page<?> ignored = PageHelper.startPage(pageNumber, pageSize)) {
            return new PageInfo<>(watchmanMapperService.list());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Resource> getResourceList(Long watchmanId) throws JsonProcessingException {
        List<Resource> result = new ArrayList<>(0);
        // 找到最后一次上报的数据
        try (Page<Resource> ignored = PageHelper.startPage(1, 1)) {
            // 查询条件 - 资源类型的上报
            Wrapper<WatchmanEventEntity> wrapper = new LambdaQueryWrapper<WatchmanEventEntity>()
                .orderByDesc(WatchmanEventEntity::getTime)
                .eq(WatchmanEventEntity::getType, WatchmanEventType.RESOURCE.getCode())
                .eq(WatchmanEventEntity::getWatchmanId, watchmanId);
            // 执行SQL
            PageInfo<WatchmanEventEntity> watchmanEventList = new PageInfo<>(watchmanEventMapper.selectList(wrapper));
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
    public boolean register(String ref, String refSign) {
        // TODO 签名校验
        Long count = watchmanMapperService.lambdaQuery().eq(WatchmanEntity::getRefSign, refSign).count();
        // 已存在返回TRUE
        if (count > 0) {return true;}
        return watchmanMapperService.save(new WatchmanEntity() {{
            setRef(ref);
            setRefSign(refSign);
        }});
    }
}
