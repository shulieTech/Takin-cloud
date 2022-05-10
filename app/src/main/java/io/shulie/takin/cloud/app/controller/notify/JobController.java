package io.shulie.takin.cloud.app.controller.notify;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.model.response.ApiResult;

/**
 * 任务上报
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Tag(name = "任务上报")
@RequestMapping("/notify/job")
@RestController("NotiftJobExampleController")
public class JobController {
    @javax.annotation.Resource
    JobService jobService;

    /**
     * 启动
     *
     * @param id 任务主键
     * @return -
     */
    @GetMapping("start")
    @Operation(summary = "启动")
    public ApiResult<Object> startJob(@Parameter(description = "任务主键", required = true) @RequestParam Long id) {
        jobService.onStart(id);
        return ApiResult.success();
    }

    /**
     * 停止
     *
     * @param id 任务主键
     * @return -
     */
    @GetMapping("stop")
    @Operation(summary = "停止")
    public ApiResult<Object> stop(@Parameter(description = "任务主键", required = true) @RequestParam Long id) {
        jobService.onStop(id);
        return ApiResult.success();
    }
}
