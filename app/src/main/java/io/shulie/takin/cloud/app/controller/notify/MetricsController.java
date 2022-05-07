package io.shulie.takin.cloud.app.controller.notify;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.util.StrUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.app.util.IpUtils;
import io.shulie.takin.cloud.app.entity.JobEntity;
import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.model.request.MetricsInfo;
import io.shulie.takin.cloud.app.service.MetricsService;
import io.shulie.takin.cloud.app.entity.JobExampleEntity;

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
    JobService jobService;
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

    @PostMapping("upload_old")
    @Operation(summary = "聚合上报-旧模式")
    public ApiResult<?> uploadByOld(
        @Parameter(description = "任务主键", required = true) @RequestParam Long jobId,
        @Parameter(description = "聚合的指标数据", required = true) @RequestBody List<MetricsInfo> data,
        HttpServletRequest request) {
        if (data.size() == 0) {return ApiResult.fail("上报的数据为空集合");}
        JobEntity jobEntity = jobService.jobEntity(jobId);
        if (jobEntity == null) {return ApiResult.fail(StrUtil.format("未找到任务:{}对应的任务", jobId));}
        String jobExampleNumberString = data.get(0).getPodNo();
        Integer jobExampleNumber = Integer.parseInt(jobExampleNumberString);
        // 根据任务和任务实例编号找到任务实例
        JobExampleEntity jobExampleEntity = jobService.jobExampleEntityList(jobId).stream().filter(t -> t.getNumber().equals(jobExampleNumber)).findFirst().orElse(null);
        if (jobExampleEntity == null) {return ApiResult.fail(StrUtil.format("未找到任务:{}对应,实例编号:{}对应的任务实例", jobId, jobExampleNumberString));}
        // 执行暨定方法
        return upload(jobId, jobExampleEntity.getId(), data, request);
    }
}
