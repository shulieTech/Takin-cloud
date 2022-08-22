package io.shulie.takin.cloud.app.controller.job;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.service.CalibrationService;

/**
 * 数据校准任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@RestController
@Tag(name = "数据校准任务")
@RequestMapping("/job/calibration")
public class CalibrationController {
    @Resource
    CalibrationService calibrationService;

    @Operation(summary = "下发")
    @GetMapping(value = "announce")
    public ApiResult<Long> check(@Parameter(description = "任务主键") @RequestParam Long pressureId) {
        return ApiResult.success(calibrationService.create(pressureId));
    }

}
