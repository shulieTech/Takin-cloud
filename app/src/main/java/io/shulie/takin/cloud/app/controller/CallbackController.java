package io.shulie.takin.cloud.app.controller;

import io.shulie.takin.cloud.app.model.response.ApiResult;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.shulie.takin.cloud.constant.enums.EventType;
import io.shulie.takin.cloud.app.entity.WatchmanEntity;
import io.shulie.takin.cloud.app.service.WatchmanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.shulie.takin.cloud.app.model.callback.ResourceUpload;

/**
 * 回调控制器
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@RestController
public class CallbackController {

    @javax.annotation.Resource
    WatchmanService watchmanService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 所有事件全部回调到这里
     *
     * @return 回调响应
     */
    @PostMapping("/callback")
    public ApiResult<?> index(
        @Parameter(description = "类型", required = true) @RequestParam Integer type,
        @Parameter(description = "关键词签名", required = true) @RequestParam String refSign,
        @RequestBody String content) {
        try {
            WatchmanEntity entity = watchmanService.ofRefSign(refSign);
            if (entity == null) {return ApiResult.fail("调度机未上报");}
            long watchmanId = entity.getId();
            EventType typeEnum = EventType.of(type);
            switch (typeEnum) {
                case WATCHMAN_UPLOAD:
                    watchmanService.upload(watchmanId, objectMapper.readValue(content, ResourceUpload.class));
                    break;
                case WATCHMAN_HEARTBEAT:
                    // TODO 调度心跳上报
                    break;
                case RESOUECE_EXAMPLE_HEARTBEAT:
                    // TODO 资源实例心跳上报
                    break;
                case RESOUECE_EXAMPLE_START:
                    // TODO 资源实例启动
                    break;
                case RESOUECE_EXAMPLE_STOP:
                    // TODO 资源实例停止
                    break;
                default:
                    return ApiResult.fail("未识别的事件类型");
            }
            return ApiResult.success();
        } catch (JsonProcessingException e) {
            log.error("事件上报失败.\n", e);
            return ApiResult.fail("JSON解析失败");
        } finally {
            log.info("事件上报信息:\n{}\n{}\n{}", type, refSign, content);
        }
    }
}
