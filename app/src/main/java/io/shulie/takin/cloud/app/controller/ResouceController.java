package io.shulie.takin.cloud.app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;

import io.shulie.takin.cloud.app.entity.ResourceExample;
import io.shulie.takin.cloud.app.entity.ResourceExampleEvent;
import io.shulie.takin.cloud.app.entity.WatchmanEvent;
import io.shulie.takin.cloud.app.mapper.ResourceExampleEventMapper;
import io.shulie.takin.cloud.app.mapper.ResourceExampleMapper;
import io.shulie.takin.cloud.app.mapper.WatchmanEventMapper;
import io.shulie.takin.cloud.app.mapper.WatchmanMapper;
import io.shulie.takin.cloud.app.service.ResourceService;
import lombok.extern.slf4j.Slf4j;

import io.shulie.takin.cloud.app.entity.Watchman;
import io.shulie.takin.cloud.app.mapper.JobMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 资源
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@RestController
@Api(tags = "资源")
@RequestMapping("/resource")
public class ResouceController {
    @Resource
    JobMapper jobMapper;
    @Resource
    WatchmanMapper watchmanMapper;
    @Resource
    WatchmanEventMapper watchmanEventMapper;

    @Resource
    ResourceService resourceService;
    @Resource
    ResourceExampleEventMapper resourceExampleEventMapper;

    @ApiOperation("调度器列表")
    @RequestMapping(value = "watchman/list", method = {RequestMethod.GET})
    public Page<Watchman> watchmanList(@ApiParam("分页页码") Integer pageNumber, @ApiParam("分页容量") Integer pageSize) {
        try (Page<Object> pageHelper = PageHelper.startPage(pageNumber, pageSize)) {
            Page<Watchman> watchmanList = pageHelper.doSelectPage(() -> watchmanMapper.selectList(null));
            PageInfo<Watchman> pageInfo = new PageInfo<>(watchmanList);
            log.info("总数:{}", pageInfo);
            return watchmanList;
        }
    }

    @ApiOperation("调度器资源")
    @RequestMapping(value = "watchman/resource", method = {RequestMethod.GET})
    public Object watchmanResource(@ApiParam("调度主键") Long watchmanId) {
        // 找到最后一次上报的数据
        try (Page<Object> pageHelper = PageHelper.startPage(1, 1)) {
            // 查询条件 - 资源类型的上报
            Wrapper<WatchmanEvent> wrapper = new LambdaQueryWrapper<WatchmanEvent>()
                .orderByDesc(WatchmanEvent::getTime)
                .eq(WatchmanEvent::getType, "")
                .eq(WatchmanEvent::getWatchmanId, watchmanId);
            // 执行SQL
            Page<WatchmanEvent> watchmanEventList = pageHelper.doSelectPage(() -> watchmanEventMapper.selectList(wrapper));
            if (watchmanEventList.size() > 0) {
                // 组装返回数据
                HashMap<String, Object> eventContext = watchmanEventList.get(0).getContext();
                return eventContext.get("data");
            }
        }
        return null;
    }

    @ApiOperation("压力机明细")
    @RequestMapping(value = "watchman/resource/example", method = {RequestMethod.GET})
    public List<Object> watchmanResourceExample(@ApiParam("资源主键") Long resourceId) {
        List<ResourceExample> resourceExamples = resourceService.listExample(resourceId);
        List<Object> result = new ArrayList<>(resourceExamples.size());
        resourceExamples.forEach(t -> {
            // 找到最后一次上报的数据
            try (Page<Object> pageHelper = PageHelper.startPage(1, 1)) {
                // 查询条件 - 资源类型的上报
                Wrapper<ResourceExampleEvent> wrapper = new LambdaQueryWrapper<ResourceExampleEvent>()
                    .orderByDesc(ResourceExampleEvent::getTime)
                    .eq(ResourceExampleEvent::getType, "")
                    .eq(ResourceExampleEvent::getResourceExampleId, t.getId());
                // 执行SQL
                Page<WatchmanEvent> watchmanEventList = pageHelper.doSelectPage(() -> resourceExampleEventMapper.selectList(wrapper));
                if (watchmanEventList.size() > 0) {
                    // 组装返回数据
                    HashMap<String, Object> eventContext = watchmanEventList.get(0).getContext();
                    result.add(eventContext.get("data"));
                }
            }
        });
        return result;
    }

}
