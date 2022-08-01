package io.shulie.takin.cloud.app.controller.notify;

import java.util.Map;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageInfo;
import cn.hutool.core.text.CharSequenceUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.constant.Message;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.data.entity.CommandEntity;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.data.entity.WatchmanEntity;
import io.shulie.takin.cloud.constant.enums.CommandType;
import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.app.service.WatchmanService;

/**
 * 命令
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j(topic = "NOTIFY")
@Tag(name = "指令确认上报")
@RequestMapping("/notify/command")
@RestController("NotiftCommandController")
public class CommandController {
    @javax.annotation.Resource
    JsonService jsonService;
    @javax.annotation.Resource
    CommandService commandService;
    @javax.annotation.Resource
    WatchmanService watchmanService;

    @PostMapping("ack")
    @Operation(summary = "指令确认")
    public ApiResult<Object> ack(@Parameter(description = "命令主键", required = true) @RequestParam Long id,
        @Parameter(description = "指令确认内容", required = true) @RequestBody String content) {
        return ApiResult.success(commandService.ack(id, "callback", content));
    }

    @GetMapping("pop")
    @Operation(summary = "弹出一条命令")
    public ApiResult<Object> ack(@Parameter(description = "关键词签名", required = true) @RequestParam String refSign,
        @Parameter(description = "命令类型", required = true) @RequestParam Integer type) {
        // 兑换命令类型
        CommandType commandType = CommandType.of(type);
        if (commandType == null) {throw new IllegalArgumentException(CharSequenceUtil.format(Message.UNKOWN_COMMAND_TYPE, type));}
        WatchmanEntity entity = watchmanService.ofRefSign(refSign);
        PageInfo<CommandEntity> range = commandService.range(entity.getId(), 1, commandType);
        // 没有命令则返回 null
        if (range.getSize() == 0) {return ApiResult.success();}
        // 有命令则返回命令内容
        CommandEntity commandEntity = range.getList().get(0);
        // pop模式要自动完成
        commandService.ack(commandEntity.getId(), "pop", DateUtil.now() + "(pop-ack)");
        Object content = jsonService.readValue(commandEntity.getContent(), Object.class);
        // 返回命令内容
        Map<String, Object> result = new HashMap<>(4);
        result.put("content", content);
        result.put("id", commandEntity.getId());
        result.put("type", commandEntity.getType());
        result.put("createTime", commandEntity.getCreateTime().getTime());
        return ApiResult.success(result);
    }
}
