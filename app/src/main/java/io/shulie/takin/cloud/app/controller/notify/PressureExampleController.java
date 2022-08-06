package io.shulie.takin.cloud.app.controller.notify;

import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.service.PressureExampleService;

/**
 * 施压任务实例上报
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j(topic = "NOTIFY")
@Tag(name = "任务实例上报")
@RequestMapping("/notify/pressure/example")
@RestController("NotiftPressureExampleController")
public class PressureExampleController {
    @javax.annotation.Resource
    PressureExampleService pressureExampleService;

    /**
     * 心跳
     *
     * @param id 施压任务实例主键
     * @return -
     */
    @GetMapping("heartbeat")
    @Operation(summary = "心跳")
    public ApiResult<Object> heartbeat(@Parameter(description = "任务实例主键", required = true) @RequestParam Long id) {
        pressureExampleService.onHeartbeat(id);
        return ApiResult.success();
    }

    /**
     * 启动
     *
     * @param id 施压任务实例主键
     * @return -
     */
    @GetMapping("start")
    @Operation(summary = "启动")
    public ApiResult<Object> start(@Parameter(description = "任务实例主键", required = true) @RequestParam Long id) {
        pressureExampleService.onStart(id);
        return ApiResult.success();
    }

    /**
     * 停止
     *
     * @param id 施压任务实例主键
     * @return -
     */
    @GetMapping("stop")
    @Operation(summary = "停止")
    public ApiResult<Object> stop(@Parameter(description = "任务实例主键", required = true) @RequestParam Long id) {
        pressureExampleService.onStop(id);
        return ApiResult.success();
    }

    /**
     * 发生异常
     *
     * @param id      施压任务实例主键
     * @param content 异常信息(字符串)
     * @return -
     */
    @PostMapping("error")
    @Operation(summary = "发生异常")
    public ApiResult<Object> error(@Parameter(description = "任务实例主键", required = true) @RequestParam Long id,
        @Parameter(description = "异常信息", required = true) @RequestBody String content) {
        pressureExampleService.onError(id, content);
        return ApiResult.success();
    }
}
