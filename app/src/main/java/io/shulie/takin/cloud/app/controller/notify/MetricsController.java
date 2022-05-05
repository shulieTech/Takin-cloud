package io.shulie.takin.cloud.app.controller.notify;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.app.util.IpUtils;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.model.request.MetricsInfo;
import io.shulie.takin.cloud.app.service.MetricsService;

/**
 * 指标数据
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Tag(name = "指标数据上报")
@RequestMapping("/notify/metrics")
@RestController("NotiftMetricsController")
public class MetricsController {
    @javax.annotation.Resource
    MetricsService metricsService;

    @PostMapping("upload")
    @Operation(summary = "聚合上报")
    public ApiResult<?> upload(
        @Parameter(description = "任务主键", required = true) @RequestParam Long jobId,
        @Parameter(description = "任务实例主键", required = true) @RequestParam Long jobExampleId,
        @Parameter(description = "聚合的指标数据", required = true) @RequestBody List<MetricsInfo> data,
        HttpServletRequest request) {
        metricsService.upload(jobId, jobExampleId, data, IpUtils.getIp(request));
        return ApiResult.success();
    }
}
