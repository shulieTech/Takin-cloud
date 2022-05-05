package io.shulie.takin.cloud.app.controller.notify;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.service.CommandService;

/**
 * 命令
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Tag(name = "指令确认上报")
@RequestMapping("/notify/command")
@RestController("NotiftCommandController")
public class CommandController {
    @javax.annotation.Resource
    CommandService commandService;

    /**
     * 命令ack
     *
     * @param id      命令主键
     * @param content ack内容
     * @return -
     */
    @PostMapping("ack")
    @Operation(summary = "指令确认")
    public ApiResult<?> ack(@Parameter(description = "命令主键", required = true) @RequestParam Long id,
        @Parameter(description = "指令确认内容", required = true) @RequestBody String content) {
        return ApiResult.success(commandService.ack(id, content));
    }
}
