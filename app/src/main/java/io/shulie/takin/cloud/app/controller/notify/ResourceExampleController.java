package io.shulie.takin.cloud.app.controller.notify;

import io.shulie.takin.cloud.model.request.job.resource.ResourceExampleInfoRequest;
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
import io.shulie.takin.cloud.app.service.ResourceExampleService;

/**
 * 资源实例上报
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j(topic = "NOTIFY")
@Tag(name = "资源实例上报")
@RequestMapping("/notify/job/resource/example")
@RestController("NotiftResourceExampleController")
public class ResourceExampleController {
    @javax.annotation.Resource
    ResourceExampleService resourceExampleService;

    /**
     * 心跳
     *
     * @param id 资源实例主键
     * @return -
     */
    @GetMapping("heartbeat")
    @Operation(summary = "心跳")
    public ApiResult<Object> heartbeat(@Parameter(description = "资源实例主键", required = true) @RequestParam Long id) {
        resourceExampleService.onHeartbeat(id);
        return ApiResult.success();
    }

    /**
     * 资源实例启动
     *
     * @param id 资源实例主键
     * @return -
     */
    @GetMapping("start")
    @Operation(summary = "启动")
    public ApiResult<Object> start(@Parameter(description = "资源实例主键", required = true) @RequestParam Long id) {
        resourceExampleService.onStart(id);
        return ApiResult.success();
    }

    /**
     * 资源实例停止
     *
     * @param id 资源实例主键
     * @return -
     */
    @GetMapping("stop")
    @Operation(summary = "停止")
    public ApiResult<Object> stop(@Parameter(description = "资源实例主键", required = true) @RequestParam Long id) {
        resourceExampleService.onStop(id);
        return ApiResult.success();
    }

    /**
     * 资源实例发生异常
     *
     * @param id      资源实例主键
     * @param content 错误信息(字符串)
     * @return -
     */
    @PostMapping("error")
    @Operation(summary = "发生异常")
    public ApiResult<Object> error(@Parameter(description = "资源实例主键", required = true) @RequestParam Long id,
                                   @Parameter(description = "异常信息", required = true) @RequestBody String content) {
        resourceExampleService.onError(id, content);
        return ApiResult.success();
    }

    /**
     * 资源实例信息和异常上报
     *
     * @param id      资源实例主键
     * @param request 上报的信息内容
     * @return -
     */
    @PostMapping("infoAndError")
    @Operation(summary = "信息和异常上报")
    public ApiResult<Object> infoAndError(@Parameter(description = "资源实例主键", required = true) @RequestParam Long id,
        @Parameter(description = "资源实例信息", required = true) @RequestBody ResourceExampleInfoRequest request) {
        // 上报信息
        resourceExampleService.onInfo(id, request);
        return ApiResult.success();
    }
}
