package io.shulie.takin.cloud.app.controller;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.model.response.JobConfig;
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
    @Resource
    JobService jobService;

    @Operation(summary = "启动任务")
    @RequestMapping(value = "start", method = {RequestMethod.POST})
    public ApiResult<String> start(@RequestBody StartRequest info) throws JsonProcessingException {
        return ApiResult.success(jobService.start(info));
    }

    @Operation(summary = "停止任务")
    @RequestMapping(value = "stop", method = {RequestMethod.GET})
    public ApiResult<?> stop(@Parameter(description = "任务主键") Long taskId) {
        jobService.stop(taskId);
        return ApiResult.success();
    }

    @Operation(summary = "查看配置")
    @RequestMapping(value = "config/get", method = {RequestMethod.GET})
    public ApiResult<Object> getConfig(@Parameter(description = "任务主键") Long taskId) {
        return ApiResult.success(jobService.getConfig(taskId));
    }

    @Operation(summary = "修改配置")
    @RequestMapping(value = "config/modify", method = {RequestMethod.POST})
    public ApiResult<?> modifyConfig(@Parameter(description = "任务主键") Long taskId,
        @RequestBody JobConfig info) throws JsonProcessingException {
        jobService.modifyConfig(taskId, info);
        return ApiResult.success();
    }
}
