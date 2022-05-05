package io.shulie.takin.cloud.app.controller.notify;

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
    public ApiResult<?> heartbeat(@Parameter(description = "任务实例主键", required = true) @RequestParam Long id) {
        jobExampleServer.onHeartbeat(id);
        return ApiResult.success();
    }

    /**
     * 心跳
     *
     * @param id 资源实例主键
     * @return -
     */
    @GetMapping("start")
    public ApiResult<?> start(@Parameter(description = "任务实例主键", required = true) @RequestParam Long id) {
        jobExampleServer.onStart(id);
        return ApiResult.success();
    }

    /**
     * 心跳
     *
     * @param id 资源实例主键
     * @return -
     */
    @GetMapping("stop")
    public ApiResult<?> stop(@Parameter(description = "任务实例主键", required = true) @RequestParam Long id) {
        jobExampleServer.onStop(id);
        return ApiResult.success();
    }

    /**
     * 心跳
     *
     * @param id      资源实例主键
     * @param content 错误信息(字符串)
     * @return -
     */
    @PostMapping("error")
    public ApiResult<?> error(@Parameter(description = "任务实例主键", required = true) @RequestParam Long id, @RequestBody String content) {
        jobExampleServer.onError(id, content);
        return ApiResult.success();
    }
}
