package io.shulie.takin.cloud.app.controller.job;

import io.shulie.takin.cloud.app.service.SlaService;
import io.shulie.takin.cloud.model.request.job.pressure.SlaNotifyRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.service.PressureService;
import io.shulie.takin.cloud.model.request.job.pressure.StartRequest;

/**
 * 发压任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Tag(name = "发压任务")
@RequestMapping("/job/pressure")
@RestController("JobPressureController")
public class PressureController {
    @javax.annotation.Resource
    PressureService pressureService;
    @javax.annotation.Resource
    SlaService service;

    @Operation(summary = "启动")
    @PostMapping(value = "start")
    public ApiResult<String> start(@RequestBody StartRequest info) {
        return ApiResult.success(pressureService.start(info));
    }

    @Operation(summary = "停止")
    @GetMapping("stop")
    public ApiResult<Object> stop(@Parameter(description = "任务主键") Long pressureId) {
        pressureService.stop(pressureId);
        return ApiResult.success();
    }

    @Operation(summary = "sla中断消息通知")
    @PostMapping(value = "slaNotify")
    public ApiResult<String> slaNotify(@RequestBody SlaNotifyRequest info) {
        service.event(info.getPressureId(), info.getSlaInfoList());
        return ApiResult.success();
    }
}
