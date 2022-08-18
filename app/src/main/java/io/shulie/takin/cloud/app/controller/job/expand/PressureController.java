package io.shulie.takin.cloud.app.controller.job.expand;

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
import io.shulie.takin.cloud.app.service.PressureService;
import io.shulie.takin.cloud.model.response.PressureConfig;
import io.shulie.takin.cloud.model.request.job.pressure.ModifyConfig;

/**
 * 发压任务拓展
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Tag(name = "发压任务拓展")
@RequestMapping("/job/expand/pressure")
@RestController("JobExpandPressureController")
public class PressureController {
    @javax.annotation.Resource
    PressureService pressureService;

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