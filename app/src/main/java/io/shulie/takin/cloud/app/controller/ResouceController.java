package io.shulie.takin.cloud.app.controller;

import java.util.List;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.model.request.ApplyResourceRequest;
import io.shulie.takin.cloud.model.resource.ResourceExampleOverview;

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
    ResourceService resourceService;

    @Operation(summary = "资源实例明细(压力机明细)")
    @RequestMapping(value = "example/list", method = {RequestMethod.GET})
    public ApiResult<List<ResourceExampleOverview>> watchmanResourceExample(
        @Parameter(description = "资源主键") Long resourceId,
        @Parameter(description = "任务主键") Long jobId) throws JsonProcessingException {
        List<ResourceExampleEntity> resourceExampleList = resourceService.listExample(resourceId, jobId);
        List<ResourceExampleOverview> result = new ArrayList<>(resourceExampleList.size());
        for (ResourceExampleEntity t : resourceExampleList) {
            result.add(resourceService.exampleOverview(t.getId()));
        }
        return ApiResult.success(result);
    }

    @Operation(summary = "资源校验")
    @RequestMapping(value = "check", method = {RequestMethod.POST})
    public ApiResult<Boolean> check(@RequestBody ApplyResourceRequest apply) throws JsonProcessingException {
        return ApiResult.success(resourceService.check(apply));
    }

    @Operation(summary = "资源锁定")
    @RequestMapping(value = "lock", method = {RequestMethod.POST})
    public ApiResult<?> lock(@RequestBody ApplyResourceRequest apply) throws JsonProcessingException {
        String resourceId = resourceService.lock(apply);
        // 预检失败，直接返回失败信息
        if (resourceId == null) {return ApiResult.fail("[预检]资源不足");}
        // 预检通过，直接返回资源主键。剩余的步骤通过异步回调处理
        else {return ApiResult.success(resourceId);}
    }

    @Operation(summary = "资源释放")
    @RequestMapping(value = "unlock", method = {RequestMethod.GET})
    public ApiResult<?> unlock(@Parameter(description = "资源主键") @RequestParam Long resourceId) {
        resourceService.unlock(resourceId);
        return ApiResult.success(resourceId);
    }
}
