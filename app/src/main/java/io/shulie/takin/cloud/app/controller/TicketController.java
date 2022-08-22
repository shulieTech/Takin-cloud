package io.shulie.takin.cloud.app.controller;

import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.service.TicketService;
import io.shulie.takin.cloud.data.entity.WatchmanEntity;
import io.shulie.takin.cloud.app.service.WatchmanService;

/**
 * ticket
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@RestController
@Tag(name = "Ticket")
@RequestMapping("/ticket")
public class TicketController {
    @javax.annotation.Resource
    TicketService ticketService;
    @javax.annotation.Resource
    WatchmanService watchmanService;

    @Operation(summary = "生成Ticket")
    @GetMapping("generate")
    public ApiResult<String> generate(
        @Parameter(description = "签名", required = true) @RequestParam String sign) {
        // 0. 根据入参找到对应的调度机公钥
        WatchmanEntity watchmanEntity = watchmanService.ofSign(sign);
        // 1. 生成随机Ticket
        String ticket = ticketService.generate();
        // 2. ticket 存入内存并使用公钥加密
        String encryptTicket = ticketService.encrypt(watchmanEntity.getId().toString(), ticket, watchmanEntity.getPublicKey());
        // 4. 返回数据
        return ApiResult.success(encryptTicket);
    }

    @Operation(summary = "更新ticket-会加验签")
    @GetMapping("update")
    public ApiResult<String> update(
        @Parameter(description = "签名", required = true) @RequestParam String sign) {
        return this.generate(sign);
    }
}
