package io.shulie.takin.cloud.app.controller.notify;

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
    public ApiResult<?> ack(@Parameter(description = "命令主键", required = true) @RequestParam Long id, @RequestBody String content) {
        return ApiResult.success(commandService.ack(id, content));
    }
}
