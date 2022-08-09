package io.shulie.takin.cloud.app.conf.ticket;

import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.http.MediaType;
import org.springframework.core.MethodParameter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import io.shulie.takin.cloud.app.service.TicketService;
import io.shulie.takin.cloud.app.service.WatchmanService;

/**
 * Ticket头响应
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@ControllerAdvice("io.shulie.takin.cloud.app.controller.notify")
public class TicketHeaderAdvice implements ResponseBodyAdvice<Object> {
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
    public Object beforeBodyWrite(Object body,
        @NonNull MethodParameter returnType,
        @NonNull MediaType selectedContentType,
        @NonNull Class selectedConverterType,
        ServerHttpRequest request, ServerHttpResponse response) {
        long timestamp = System.currentTimeMillis();
        String watchmanSign = request.getHeaders().getFirst("WATCHMAN-SIGN");
        // 获取调度器
        Long watchamanId = watchmanService.ofSign(watchmanSign).getId();
        // 获取调度器的当前ticket
        String ticket = ticketService.get(watchamanId.toString());
        // 计算签名
        String sign = ticketService.sign(null, timestamp, ticket);
        // 写入请求头
        response.getHeaders().set("TICKET-SIGN", sign);
        response.getHeaders().set("TICKET-TIMESTAMP", Long.toString(timestamp));
        // 原样返回body
        return body;
    }
}
