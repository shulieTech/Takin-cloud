package io.shulie.takin.cloud.app.controller.job;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.model.request.job.pressure.StartRequest;
import io.shulie.takin.cloud.app.service.PressureService;
import io.shulie.takin.cloud.model.response.PressureConfig;
import io.shulie.takin.cloud.model.request.job.pressure.ModifyConfig;

/**
 * 发压任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Tag(name = "发压任务")
@RestController
@RequestMapping("/job/pressure")
public class PressureController {
    @javax.annotation.Resource
    PressureService pressureService;

    @Operation(summary = "启动任务")
    @PostMapping(value = "start")
    public ApiResult<String> start(@RequestBody StartRequest info) {
        return ApiResult.success(pressureService.start(info));
    }

    @Operation(summary = "停止任务")
    @GetMapping("stop")
    public ApiResult<Object> stop(@Parameter(description = "任务主键") Long pressureId) {
        pressureService.stop(pressureId);
        return ApiResult.success();
    }

    @Operation(summary = "查看配置")
    @GetMapping("config/get")
    public ApiResult<List<PressureConfig>> getConfig(@Parameter(description = "任务主键") Long pressureId,
        @Parameter(description = "ref(可以不传)") String ref) {
        return ApiResult.success(pressureService.getConfig(pressureId, ref));
    }

    @Operation(summary = "修改配置")
    @PostMapping(value = "config/modify")
    public ApiResult<Object> modifyConfig(@RequestBody ModifyConfig info) {
        pressureService.modifyConfig(info.getPressureId(), info);
        return ApiResult.success();
    }
}
