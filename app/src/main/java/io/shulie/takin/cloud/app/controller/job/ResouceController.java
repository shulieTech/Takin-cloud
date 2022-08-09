package io.shulie.takin.cloud.app.controller.job;

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
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.model.request.job.resource.ApplyResourceRequest;

/**
 * 资源
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Tag(name = "资源任务")
@RequestMapping("/job/resource")
@RestController("JobResouceController")
public class ResouceController {
    @javax.annotation.Resource
    ResourceService resourceService;

    @Operation(summary = "锁定")
    @PostMapping(value = "lock")
    public ApiResult<Object> lock(@RequestBody ApplyResourceRequest apply) {
        String resourceId = resourceService.lock(apply);
        // 预检失败，直接返回失败信息
        if (resourceId == null) {return ApiResult.fail(Message.RESOURCE_SHORTAGE);}
        // 预检通过，直接返回资源主键。剩余的步骤通过异步回调处理
        else {return ApiResult.success(resourceId);}
    }

    @Operation(summary = "释放")
    @GetMapping("unlock")
    public ApiResult<Object> unlock(@Parameter(description = "资源主键") @RequestParam Long resourceId) {
        resourceService.unlock(resourceId);
        return ApiResult.success(resourceId);
    }
}
