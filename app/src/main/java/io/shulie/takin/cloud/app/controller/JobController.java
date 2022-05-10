package io.shulie.takin.cloud.app.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.model.response.JobConfig;
import io.shulie.takin.cloud.model.request.ModifyConfig;
import io.shulie.takin.cloud.model.request.StartRequest;

/**
 * 任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Tag(name = "任务")
@RestController
@RequestMapping("/job")
public class JobController {
    @javax.annotation.Resource
    JobService jobService;

    @Operation(summary = "启动任务")
    @PostMapping(value = "start")
    public ApiResult<String> start(@RequestBody StartRequest info) {
        return ApiResult.success(jobService.start(info));
    }

    @Operation(summary = "停止任务")
    @GetMapping("stop")
    public ApiResult<Object> stop(@Parameter(description = "任务主键") Long jobId) {
        jobService.stop(jobId);
        return ApiResult.success();
    }

    @Operation(summary = "查看配置")
    @GetMapping("config/get")
    public ApiResult<List<JobConfig>> getConfig(@Parameter(description = "任务主键") Long jobId,
        @Parameter(description = "ref(可以不传)") String ref) {
        return ApiResult.success(jobService.getConfig(jobId, ref));
    }

    @Operation(summary = "修改配置")
    @PostMapping(value = "config/modify")
    public ApiResult<Object> modifyConfig(@RequestBody ModifyConfig info) {
        jobService.modifyConfig(info.getJobId(), info);
        return ApiResult.success();
    }
}
