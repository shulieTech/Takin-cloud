package io.shulie.takin.cloud.app.controller.notify;

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
import io.shulie.takin.cloud.app.service.JobExampleServer;

/**
 * 任务实例上报
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Tag(name = "任务实例上报")
@RequestMapping("/notify/job_example")
@RestController("NotiftJobExampleController")
public class JobExampleController {
    @javax.annotation.Resource
    JobExampleServer jobExampleServer;

    /**
     * 心跳
     *
     * @param id 资源实例主键
     * @return -
     */
    @GetMapping("heartbeat")
    @Operation(summary = "心跳")
    public ApiResult<?> heartbeat(@Parameter(description = "任务实例主键", required = true) @RequestParam Long id) {
        jobExampleServer.onHeartbeat(id);
        return ApiResult.success();
    }

    /**
     * 启动
     *
     * @param id 资源实例主键
     * @return -
     */
    @GetMapping("start")
    @Operation(summary = "启动")
    public ApiResult<?> start(@Parameter(description = "任务实例主键", required = true) @RequestParam Long id) {
        jobExampleServer.onStart(id);
        return ApiResult.success();
    }

    /**
     * 停止
     *
     * @param id 资源实例主键
     * @return -
     */
    @GetMapping("stop")
    @Operation(summary = "停止")
    public ApiResult<?> stop(@Parameter(description = "任务实例主键", required = true) @RequestParam Long id) {
        jobExampleServer.onStop(id);
        return ApiResult.success();
    }

    /**
     * 发生异常
     *
     * @param id      资源实例主键
     * @param content 异常信息(字符串)
     * @return -
     */
    @PostMapping("error")
    @Operation(summary = "发生异常")
    public ApiResult<?> error(@Parameter(description = "任务实例主键", required = true) @RequestParam Long id,
        @Parameter(description = "异常信息", required = true) @RequestBody String content) {
        jobExampleServer.onError(id, content);
        return ApiResult.success();
    }
}
