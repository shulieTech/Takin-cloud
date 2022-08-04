package io.shulie.takin.cloud.app.controller;

import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.service.TicketService;

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

    @Operation(summary = "请求ticket")
    @PostMapping("create")
    public ApiResult<String> createTicket(Long watchmanId) {
        // 0. 根据入参找到对应的调度机公钥
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCEsMro2KQYmTgoqEXUeSJLCNFHhfwTstrvAj8BNCF4yoAbaDjYF9r/"
            + "ZrV6j2JszyxYvPFB+N+ZCOI/hc/"
            + "59midqobbGgoxCSkOJJbsih4hnAahYUKytrxpinvkFXpGppkMpdSRnDvT7XkA2RpH5nNMCiDpjLeTGuf+8NdN6I4fSQIDAQAB";
        // 1. 生成随机Ticket
        String ticket = ticketService.generate();
        // 2. ticket 存入内存
        // 3. Ticket使用公钥加密
        String encryptTicket = ticketService.encrypt(ticket, publicKey);
        // 4. 返回数据
        return ApiResult.success(encryptTicket);
    }

    @Operation(summary = "更新ticket-会加验签")
    @PostMapping("update")
    public ApiResult<String> updateTicket() {
        String ticket = "随机字符串";
        // MD5( ticket+"内存中的TICKET")
        return ApiResult.success(ticket);
    }
}
