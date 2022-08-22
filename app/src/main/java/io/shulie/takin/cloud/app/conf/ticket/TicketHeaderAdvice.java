package io.shulie.takin.cloud.app.conf.ticket;

import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.http.MediaType;
import org.springframework.core.MethodParameter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;

import io.shulie.takin.cloud.constant.TicketConstants;
import io.shulie.takin.cloud.app.service.TicketService;
import io.shulie.takin.cloud.data.entity.WatchmanEntity;
import io.shulie.takin.cloud.app.service.WatchmanService;

/**
 * Ticket头响应
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@RestControllerAdvice("io.shulie.takin.cloud.app.controller.notify")
public class TicketHeaderAdvice extends AbstractMappingJacksonResponseBodyAdvice {
    @javax.annotation.Resource
    TicketService ticketService;
    @javax.annotation.Resource
    WatchmanService watchmanService;

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class converterType) {return true;}

    /**
     * {@inheritDoc}
     */
    @Override
    protected void beforeBodyWriteInternal(
        @NonNull org.springframework.http.converter.json.MappingJacksonValue bodyContainer,
        @NonNull MediaType contentType, @NonNull MethodParameter returnType,
        @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        long timestamp = System.currentTimeMillis();
        String watchmanSign = request.getHeaders().getFirst(TicketConstants.HEADER_WATCHMAN_SIGN);
        // 获取调度器 - 忽略未找到的异常
        WatchmanEntity watchman = null;
        try {watchman = watchmanService.ofSign(watchmanSign);} catch (IllegalArgumentException e) {log.debug(e.getMessage());}
        Long watchamanId = watchman == null ? null : watchman.getId();
        // 获取调度器的当前ticket
        String ticket = ticketService.get(String.valueOf(watchamanId));
        // 计算签名
        String sign = ticketService.sign(null, timestamp, ticket);
        // 写入请求头
        response.getHeaders().set(TicketConstants.HEADER_TICKET_SIGN, sign);
        response.getHeaders().set(TicketConstants.HEADER_TICKET_TIMESTAMP, Long.toString(timestamp));
    }
}
