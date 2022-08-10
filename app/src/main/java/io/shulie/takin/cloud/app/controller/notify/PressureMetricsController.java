package io.shulie.takin.cloud.app.controller.notify;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.text.CharSequenceUtil;

import io.shulie.takin.cloud.constant.Message;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.app.util.IpUtils;
import io.shulie.takin.cloud.data.entity.PressureEntity;
import io.shulie.takin.cloud.app.service.PressureService;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.model.request.job.pressure.MetricsInfo;
import io.shulie.takin.cloud.app.service.PressureMetricsService;
import io.shulie.takin.cloud.data.entity.PressureExampleEntity;

/**
 * 指标数据
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Tag(name = "压测指标数据上报")
@Slf4j(topic = "METRICS")
@RequestMapping("/notify/job/pressure/metrics")
@RestController("NotiftPressureMetricsController")
public class PressureMetricsController {
    @javax.annotation.Resource
    PressureService pressureService;
    @javax.annotation.Resource
    PressureMetricsService pressureMetricsService;

    @PostMapping("upload")
    @Operation(summary = "聚合上报")
    public ApiResult<Object> upload(
        @Parameter(description = "任务主键", required = true) @RequestParam Long pressureId,
        @Parameter(description = "任务实例主键", required = true) @RequestParam Long pressureExampleId,
        @Parameter(description = "聚合的指标数据", required = true) @RequestBody List<MetricsInfo> data,
        HttpServletRequest request) {
        List<MetricsInfo> filterData = data.stream().filter(t -> "response".equals(t.getType())).collect(Collectors.toList());
        if (!filterData.isEmpty()) {
            pressureMetricsService.upload(pressureId, pressureExampleId, filterData, IpUtils.getIp(request));
        }
        return ApiResult.success();
    }

    @PostMapping("upload_old")
    @Operation(summary = "聚合上报-旧模式")
    public ApiResult<Object> uploadByOld(
        @Parameter(description = "任务主键", required = true) @RequestParam Long pressureId,
        @Parameter(description = "聚合的指标数据", required = true) @RequestBody List<MetricsInfo> data,
        HttpServletRequest request) {
        if (data.isEmpty()) {return ApiResult.fail(Message.EMPTY_METRICS_LIST);}
        PressureEntity pressureEntity = pressureService.entity(pressureId);
        if (pressureEntity == null) {return ApiResult.fail(CharSequenceUtil.format(Message.MISS_PRESSURE, pressureId));}
        String pressureExampleNumberString = data.get(0).getPodNo();
        Integer pressureExampleNumber = Integer.parseInt(pressureExampleNumberString);
        // 根据任务和任务实例编号找到任务实例
        PressureExampleEntity pressureExampleEntity = pressureService.exampleEntityList(pressureId).stream().filter(t -> t.getNumber().equals(pressureExampleNumber)).findFirst().orElse(null);
        if (pressureExampleEntity == null) {
            log.warn("未找到任务:{}对应,实例编号:{}对应的任务实例", pressureId, pressureExampleNumberString);
            return ApiResult.fail(Message.MISS_RESOURCE_EXAMPLE);
        }
        // 执行暨定方法
        return upload(pressureId, pressureExampleEntity.getId(), data, request);
    }
}
