package io.shulie.takin.cloud.app.controller;

import java.util.List;
import java.util.ArrayList;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.Page;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.app.service.WatchmanService;
import io.shulie.takin.cloud.app.model.response.ApiResult;
import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.app.model.request.ApplyResourceRequest;

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
    @Resource
    ResourceService resourceService;
    @Resource
    WatchmanService watchmanService;

    @Operation(summary = "调度器列表")
    @RequestMapping(value = "watchman/list", method = {RequestMethod.GET})
    public ApiResult watchmanList(
        @Parameter(name = "分页页码", required = true) Integer pageNumber,
        @Parameter(name = "分页容量", required = true) Integer pageSize) {
        try (Page<Object> list = watchmanService.list(pageNumber, pageSize)) {
            return ApiResult.success(list.getResult(), list.getTotal());
        }
    }

    @Operation(summary = "调度器资源")
    @RequestMapping(value = "watchman/resource", method = {RequestMethod.GET})
    public ApiResult watchmanResource(@Parameter(name = "调度主键", required = true) Long watchmanId) {
        return ApiResult.success(watchmanService.getResourceList(watchmanId));
    }

    @Operation(summary = "压力机明细")
    @RequestMapping(value = "watchman/resource/example", method = {RequestMethod.GET})
    public ApiResult watchmanResourceExample(@Parameter(name = "资源主键") Long resourceId) {
        List<ResourceExampleEntity> resourceExampleList = resourceService.listExample(resourceId);
        List<Object> result = new ArrayList<>(resourceExampleList.size());
        resourceExampleList.forEach(t -> result.add(watchmanService.exampleOverview(t.getId())));
        return ApiResult.success(result);
    }

    @Operation(summary = "资源校验")
    @RequestMapping(value = "check", method = {RequestMethod.POST})
    public ApiResult check(ApplyResourceRequest apply) {
        return ApiResult.success(resourceService.check(apply));
    }

    @Operation(summary = "资源锁定")
    @RequestMapping(value = "lock", method = {RequestMethod.POST})
    public ApiResult lock(@RequestBody ApplyResourceRequest apply,
        @Parameter(name = "回调地址", required = true) String callbackUrl) {
        String resourceId = resourceService.lock(apply, callbackUrl);
        // 预检失败，直接返回失败信息
        if (resourceId == null) {return ApiResult.fail("[预检]资源不足");}
        // 预检通过，直接返回资源主键。剩余的步骤通过异步回调处理
        else {return ApiResult.success(resourceId);}
    }
}
