package io.shulie.takin.cloud.app.controller;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.service.ExcessJobService;
import io.shulie.takin.cloud.constant.enums.ExcessJobType;

/**
 * 额外的任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@RestController
@Tag(name = "额外的任务")
@RequestMapping("/excess/job")
public class ExcessJobController {
    @Resource
    ExcessJobService excessJobService;

    @Operation(summary = "提交对任务的数据校验")
    @GetMapping(value = "dataCalibration")
    public ApiResult<Long> check(@Parameter(description = "任务主键") @RequestParam Long jobId) {
        return ApiResult.success(excessJobService.create(ExcessJobType.DATA_CALIBRATION.getCode(), jobId, ""));
    }

}
