package io.shulie.takin.cloud.app.controller.notify;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.data.entity.WatchmanEntity;
import io.shulie.takin.cloud.app.service.WatchmanService;
import io.shulie.takin.cloud.model.resource.ResourceSource;

/**
 * 调度器上报
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Tag(name = "调度器上报")
@Slf4j(topic = "NOTIFY")
@RequestMapping("/notify/watchman")
@RestController("NotiftWatchmanController")
public class WatchmanController {
    @javax.annotation.Resource
    WatchmanService watchmanService;

    /**
     * 心跳
     *
     * @param refSign 签名
     * @return -
     */

    @GetMapping("heartbeat")
    @Operation(summary = "心跳")
    public ApiResult<Object> heartbeat(@Parameter(description = "关键词签名", required = true) @RequestParam String refSign) {
        WatchmanEntity entity = watchmanService.ofRefSign(refSign);
        watchmanService.onHeartbeat(entity.getId());
        return ApiResult.success();
    }

    /**
     * 发生异常
     *
     * @param refSign 签名
     * @param content 异常内容(字符串)
     * @return -
     */
    @PostMapping("abnormal")
    @Operation(summary = "发生异常")
    public ApiResult<Object> abnormal(@Parameter(description = "关键词签名", required = true) @RequestParam String refSign,
        @Parameter(description = "异常信息", required = true) @RequestBody String content) {
        WatchmanEntity entity = watchmanService.ofRefSign(refSign);
        watchmanService.onAbnormal(entity.getId(), content);
        return ApiResult.success();
    }

    /**
     * 恢复正常
     *
     * @param refSign 签名
     * @return -
     */
    @GetMapping("normal")
    @Operation(summary = "恢复正常")
    public ApiResult<Object> normal(@Parameter(description = "关键词签名", required = true) @RequestParam String refSign) {
        WatchmanEntity entity = watchmanService.ofRefSign(refSign);
        watchmanService.onNormal(entity.getId());
        return ApiResult.success();
    }

    /**
     * 上报资源
     *
     * @param refSign 签名
     * @param content 资源信息(Json字符串)
     * @return -
     */
    @PostMapping("upload")
    @Operation(summary = "上报资源")
    public ApiResult<Object> upload(@Parameter(description = "关键词签名", required = true) @RequestParam String refSign,
        @Parameter(description = "资源列表", required = true) @RequestBody List<ResourceSource> content) {
        WatchmanEntity entity = watchmanService.ofRefSign(refSign);
        watchmanService.upload(entity.getId(), content);
        return ApiResult.success();
    }

}
