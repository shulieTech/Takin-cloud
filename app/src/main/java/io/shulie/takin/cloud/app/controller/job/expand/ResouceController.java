package io.shulie.takin.cloud.app.controller.job.expand;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.data.entity.PressureEntity;
import io.shulie.takin.cloud.app.service.PressureService;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.data.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.model.resource.ResourceExampleOverview;
import io.shulie.takin.cloud.model.request.job.resource.ApplyResourceRequest;

/**
 * 资源任务拓展
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Tag(name = "资源任务拓展")
@RequestMapping("/job/expand/resource")
@RestController("JobExpandResouceController")
public class ResouceController {
    @javax.annotation.Resource
    PressureService pressureService;
    @javax.annotation.Resource
    ResourceService resourceService;

    @Operation(summary = "资源实例明细(压力机明细)")
    @GetMapping("example/list")
    public ApiResult<List<ResourceExampleOverview>> watchmanResourceExample(
        @Parameter(description = "资源主键") @RequestParam Long resourceId,
        @Parameter(description = "任务主键") @RequestParam(required = false) Long pressureId) {
        if (resourceId == null && pressureId != null) {
            PressureEntity pressureEntity = pressureService.entity(pressureId);
            resourceId = pressureEntity == null ? null : pressureEntity.getResourceId();
        }
        List<ResourceExampleEntity> resourceExampleList = resourceService.listExample(resourceId);
        List<ResourceExampleOverview> result = new ArrayList<>(resourceExampleList.size());
        for (ResourceExampleEntity t : resourceExampleList) {
            result.add(resourceService.exampleOverview(t.getId()));
        }
        return ApiResult.success(result);
    }

    @Operation(summary = "资源校验")
    @PostMapping(value = "check")
    public ApiResult<Map<Long, Integer>> check(@RequestBody ApplyResourceRequest apply) {
        return ApiResult.success(resourceService.check(apply));
    }
}