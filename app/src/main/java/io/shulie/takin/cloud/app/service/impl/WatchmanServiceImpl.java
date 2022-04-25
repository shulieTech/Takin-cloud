package io.shulie.takin.cloud.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.shulie.takin.cloud.app.entity.WatchmanEvent;
import io.shulie.takin.cloud.app.mapper.WatchmanMapper;
import io.shulie.takin.cloud.app.service.WatchmanService;
import io.shulie.takin.cloud.app.mapper.WatchmanEventMapper;
import io.shulie.takin.cloud.app.entity.ResourceExampleEvent;
import io.shulie.takin.cloud.app.mapper.ResourceExampleEventMapper;

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
    @Resource
    WatchmanMapper watchmanMapper;
    @Resource
    WatchmanEventMapper watchmanEventMapper;
    @Resource
    ResourceExampleEventMapper resourceExampleEventMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Object> list(int pageNumber, int pageSize) {
        try (Page<?> pageHelper = PageHelper.startPage(pageNumber, pageSize)) {
            return pageHelper.doSelectPage(() -> watchmanMapper.selectList(null));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getResourceList(Long watchmanId) {
        List<Object> result = new ArrayList<>(0);
        // 找到最后一次上报的数据
        try (Page<Object> pageHelper = PageHelper.startPage(1, 1)) {
            // 查询条件 - 资源类型的上报
            Wrapper<WatchmanEvent> wrapper = new LambdaQueryWrapper<WatchmanEvent>()
                .orderByDesc(WatchmanEvent::getTime)
                .eq(WatchmanEvent::getType, "")
                .eq(WatchmanEvent::getWatchmanId, watchmanId);
            // 执行SQL
            Page<WatchmanEvent> watchmanEventList = pageHelper.doSelectPage(() ->
                watchmanEventMapper.selectList(wrapper));
            // 组装数据
            if (watchmanEventList.size() > 0) {
                // 组装返回数据
                HashMap<String, Object> eventContext = watchmanEventList.get(0).getContext();
                {
                    // 处理数据
                    result.add(eventContext.get("data"));
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object exampleOverview(Long resourceExampleId) {
        // 找到最后一次上报的数据
        try (Page<Object> pageHelper = PageHelper.startPage(1, 1)) {
            // 查询条件 - 资源类型的上报
            Wrapper<ResourceExampleEvent> wrapper = new LambdaQueryWrapper<ResourceExampleEvent>()
                .orderByDesc(ResourceExampleEvent::getTime)
                .eq(ResourceExampleEvent::getType, "")
                .eq(ResourceExampleEvent::getResourceExampleId, resourceExampleId);
            // 执行SQL
            Page<WatchmanEvent> watchmanEventList = pageHelper.doSelectPage(() -> resourceExampleEventMapper.selectList(wrapper));
            if (watchmanEventList.size() > 0) {
                // 组装返回数据
                HashMap<String, Object> eventContext = watchmanEventList.get(0).getContext();
                return eventContext.get("data");
            }
        }
        return null;
    }
}
