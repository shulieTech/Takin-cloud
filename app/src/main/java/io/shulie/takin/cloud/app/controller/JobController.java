package io.shulie.takin.cloud.app.controller;

import javax.annotation.Resource;

import io.shulie.takin.cloud.app.model.response.JobConfig;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.app.model.response.ApiResult;
import io.shulie.takin.cloud.app.entity.ThreadConfigExampleEntity;

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
    public ApiResult<String> start(@RequestBody Object info) {
        return ApiResult.success(jobService.start(info));
    }

    @Operation(summary = "停止任务")
    @RequestMapping(value = "stop", method = {RequestMethod.GET})
    public ApiResult<?> stop(@Parameter(name = "任务主键") Long taskId) {
        jobService.stop(taskId);
        return ApiResult.success();
    }

    @Operation(summary = "查看配置")
    @RequestMapping(value = "config/get", method = {RequestMethod.GET})
    public ApiResult<Object> getConfig(@Parameter(name = "任务主键") Long taskId) {
        return ApiResult.success(jobService.getConfig(taskId));
    }

    @Operation(summary = "修改配置")
    @RequestMapping(value = "config/modify", method = {RequestMethod.POST})
    public ApiResult<?> modifyConfig(@Parameter(name = "任务主键") Long taskId,
        @RequestBody JobConfig info) {
        jobService.modifyConfig(taskId, info);
        return ApiResult.success();
    }
}
