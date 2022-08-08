package io.shulie.takin.cloud.app.conf.ticket;

import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.http.HttpStatus;
import cn.hutool.core.exceptions.ValidateException;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.service.WatchmanService;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import org.springframework.web.servlet.ModelAndView;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.app.service.TicketService;

/**
 * 登录拦截器Handler
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@Component
public class TicketInterceptorHandler implements HandlerInterceptor {
    @Resource
    JsonService jsonService;
    @Resource
    TicketService ticketService;
    @Resource
    WatchmanService watchmanService;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
        @NonNull Object handler)
        throws Exception {
        try {
            // 获取请求头
            String ticketSign = request.getHeader("TICKET-SIGN");
            String watchmanSign = request.getHeader("WATCHMAN-SIGN");
            String ticketTimestamp = request.getHeader("TICKET-TIMESTAMP");
            // 时间戳转换
            long timestamp = Long.parseLong(ticketTimestamp + "");
            // 获取调度器
            Long watchamanId = watchmanService.ofSign(watchmanSign).getId();
            // 获取调度器的当前ticket
            String ticket = ticketService.get(watchamanId.toString());
            // 校验ticket
            if (ticketService.verification(ticket)) {
                // 重新计算签名
                String sign = ticketService.sign(null, timestamp, ticket);
                // 校验成功
                if (sign.equals(ticketSign)) {return true;}
                // 校验失败
                else {throw new ValidateException("签名校验失败.\nin:{}\ncalc:{}", ticketSign, sign);}
            } else {
                throw new ValidateException("ticket校验失败.{}", ticket);
            }
        } catch (Exception e) {
            log.error("验签失败", e);
            response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
            PrintWriter writer = response.getWriter();
            writer.write(jsonService.writeValueAsString(ApiResult.fail(e.getMessage())));
            writer.flush();
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
        @NonNull Object handler, @Nullable ModelAndView modelAndView) {
        long timestamp = System.currentTimeMillis();
        String watchmanSign = request.getHeader("WATCHMAN-SIGN");
        // 获取调度器
        Long watchamanId = watchmanService.ofSign(watchmanSign).getId();
        // 获取调度器的当前ticket
        String ticket = ticketService.get(watchamanId.toString());
        // 计算签名
        String sign = ticketService.sign(null, timestamp, ticket);
        // 写入请求头
        response.setHeader("TICKET-SIGN", sign);
    }
}