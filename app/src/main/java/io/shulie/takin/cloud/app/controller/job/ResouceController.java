package io.shulie.takin.cloud.app.controller.job;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.constant.Message;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.data.entity.PressureEntity;
import io.shulie.takin.cloud.app.service.PressureService;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.data.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.model.resource.ResourceExampleOverview;
import io.shulie.takin.cloud.model.request.job.resource.ApplyResourceRequest;

/**
 * 资源
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@RestController
@Tag(name = "资源")
@RequestMapping("/resource")
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

    @Operation(summary = "资源锁定")
    @PostMapping(value = "lock")
    public ApiResult<Object> lock(@RequestBody ApplyResourceRequest apply) {
        String resourceId = resourceService.lock(apply);
        // 预检失败，直接返回失败信息
        if (resourceId == null) {return ApiResult.fail(Message.RESOURCE_SHORTAGE);}
        // 预检通过，直接返回资源主键。剩余的步骤通过异步回调处理
        else {return ApiResult.success(resourceId);}
    }

    @Operation(summary = "资源释放")
    @GetMapping("unlock")
    public ApiResult<Object> unlock(@Parameter(description = "资源主键") @RequestParam Long resourceId) {
        resourceService.unlock(resourceId);
        return ApiResult.success(resourceId);
    }
}
