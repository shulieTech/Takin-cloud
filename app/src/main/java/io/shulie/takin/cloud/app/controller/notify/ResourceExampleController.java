package io.shulie.takin.cloud.app.controller.notify;

import java.util.HashMap;

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
    public ApiResult<?> stop(@Parameter(description = "资源实例主键", required = true) @RequestParam Long id) {
        resourceExampleService.onStop(id);
        return ApiResult.success();
    }

    /**
     * 资源实例异常
     *
     * @param id      资源实例主键
     * @param content 错误信息(字符串)
     * @return -
     */
    @PostMapping("error")
    public ApiResult<?> error(@Parameter(description = "资源实例主键", required = true) @RequestParam Long id, @RequestBody String content) {
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
    public ApiResult<?> info(@Parameter(description = "资源实例主键", required = true) @RequestParam Long id, @RequestBody HashMap<String, Object> content) {
        resourceExampleService.onInfo(id, content);
        return ApiResult.success();
    }
}
