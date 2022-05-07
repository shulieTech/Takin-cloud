package io.shulie.takin.cloud.app.controller.notify;

import java.util.HashMap;

import cn.hutool.core.util.StrUtil;

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
@Tag(name = "资源实例上报")
@RequestMapping("/notify/resource_example")
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
    public ApiResult<?> heartbeat(@Parameter(description = "资源实例主键", required = true) @RequestParam Long id) {
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
    public ApiResult<?> start(@Parameter(description = "资源实例主键", required = true) @RequestParam Long id) {
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
    public ApiResult<?> stop(@Parameter(description = "资源实例主键", required = true) @RequestParam Long id) {
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
    public ApiResult<?> error(@Parameter(description = "资源实例主键", required = true) @RequestParam Long id,
        @Parameter(description = "异常信息", required = true) @RequestBody String content) {
        resourceExampleService.onError(id, content);
        return ApiResult.success();
    }

    /**
     * 资源实例信息上报
     *
     * @param id      资源实例主键
     * @param content 上报的信息内容
     * @return -
     */
    @PostMapping("info")
    @Operation(summary = "信息上报")
    public ApiResult<?> info(@Parameter(description = "资源实例主键", required = true) @RequestParam Long id,
        @Parameter(description = "资源实例信息", required = true) @RequestBody HashMap<String, Object> content) {
        resourceExampleService.onInfo(id, content);
        return ApiResult.success();
    }

    /**
     * 资源实例信息和异常上报
     *
     * @param id      资源实例主键
     * @param content 上报的信息内容
     * @return -
     */
    @PostMapping("infoAndError")
    @Operation(summary = "信息和异常上报")
    public ApiResult<?> infoAndError(@Parameter(description = "资源实例主键", required = true) @RequestParam Long id,
        @Parameter(description = "资源实例信息", required = true) @RequestBody HashMap<String, Object> content) {
        // 提取错误信息
        final String errorFlag = "error";
        String errorMessage = content.getOrDefault(errorFlag, "").toString();
        content.remove(errorFlag);
        // 上报信息
        info(id, content);
        // 上报异常
        if (StrUtil.isNotBlank(errorMessage)) {error(id, errorMessage.trim());}
        return ApiResult.success();
    }
}
