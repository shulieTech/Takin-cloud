package io.shulie.takin.cloud.app.controller;

import java.util.Date;
import java.util.List;
import java.util.Comparator;
import java.util.LinkedList;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.model.response.Gravestone;
import io.shulie.takin.cloud.data.entity.ResourceEntity;
import io.shulie.takin.cloud.data.entity.PressureEntity;
import io.shulie.takin.cloud.app.service.PressureService;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.constant.enums.NotifyEventType;
import io.shulie.takin.cloud.data.entity.PressureExampleEntity;
import io.shulie.takin.cloud.data.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.data.entity.PressureExampleEventEntity;
import io.shulie.takin.cloud.data.entity.ResourceExampleEventEntity;
import io.shulie.takin.cloud.data.service.PressureExampleEventMapperService;
import io.shulie.takin.cloud.data.service.ResourceExampleEventMapperService;

/**
 * 墓志铭
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@RestController
@RequestMapping("/gravestone")
public class GravestoneController {
    @Resource
    PressureService pressureService;
    @Resource
    ResourceService resourceService;
    @Resource(name = "pressureExampleEventMapperServiceImpl")
    PressureExampleEventMapperService pressureExampleEventMapper;
    @Resource(name = "resourceExampleEventMapperServiceImpl")
    ResourceExampleEventMapperService resourceExampleEventMapper;

    /**
     * 读取墓志铭
     *
     * @return 内容
     */
    @RequestMapping("read")
    public ApiResult<List<Gravestone>> read(@RequestParam Long resourceId, @RequestParam(required = false) Long pressureId) {
        List<Gravestone> result = new LinkedList<>();
        ResourceEntity resource = resourceService.entity(resourceId);
        if (resource != null) {
            // 资源信息
            result.add(new Gravestone(resource.getCreateTime().getTime(), "资源创建", String.valueOf(resource.getId())));
            List<ResourceExampleEntity> resourceExample = resourceService.listExample(resource.getId());
            resourceExample.forEach(t -> {
                // 资源实例信息
                result.add(new Gravestone(t.getCreateTime().getTime(), "资源实例创建", String.valueOf(t.getId())));
                // 资源实例事件
                List<ResourceExampleEventEntity> resourceExampleEvent = resourceExampleEventMapper.lambdaQuery()
                    .eq(ResourceExampleEventEntity::getResourceExampleId, t.getId())
                    .list();
                resourceExampleEvent.forEach(c -> result.add(eventType(c.getTime(), c.getId(), c.getType(), c.getContext())));
            });
            if (pressureId != null) {
                // 施压任务
                PressureEntity pressure = pressureService.entity(pressureId);
                if (pressure != null) {
                    // 施压任务
                    result.add(new Gravestone(resource.getCreateTime().getTime(), "任务创建", String.valueOf(pressure.getId())));
                    // 施压任务实例
                    List<PressureExampleEntity> pressureExample = pressureService.exampleEntityList(resource.getId());
                    pressureExample.forEach(t -> {
                        // 施压任务实例信息
                        result.add(new Gravestone(resource.getCreateTime().getTime(), "任务实例创建", String.valueOf(t.getId())));
                        // 施压任务实例事件
                        List<PressureExampleEventEntity> pressureExampleEvent = pressureExampleEventMapper.lambdaQuery()
                            .eq(PressureExampleEventEntity::getPressureExampleId, t.getId())
                            .list();
                        pressureExampleEvent.forEach(c -> result.add(eventType(c.getTime(), c.getId(), c.getType(), c.getContext())));
                    });
                }
            }
        }
        result.sort(Comparator.comparingLong(Gravestone::getTime));
        // 返回结果
        return ApiResult.success(result);
    }

    public Gravestone eventType(Date time, long id, Integer type, String content) {
        NotifyEventType of = NotifyEventType.of(type);
        String typeString = of == null ? CharSequenceUtil.format("未知({})", type) : of.getDescription();
        String message = CharSequenceUtil.format("({}) - {}", id, content);
        return new Gravestone(time.getTime(), typeString, message);
    }
}
