package io.shulie.takin.cloud.app.conf.ticket;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.http.HttpStatus;
import cn.hutool.http.ContentType;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.exceptions.ValidateException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.constant.TicketConstants;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.service.TicketService;
import io.shulie.takin.cloud.app.service.WatchmanService;

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
    @Value("${drilling.ticket.check:false}")
    private boolean drillingTicketCheck;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
        @NonNull Object handler)
        throws Exception {
        if (!drillingTicketCheck){
            return true;
        }
        boolean result = true;
        String exceptionMessage = null;
        try {
            // 获取请求头
            String ticketSign = request.getHeader(TicketConstants.HEADER_TICKET_SIGN);
            String watchmanSign = request.getHeader(TicketConstants.HEADER_WATCHMAN_SIGN);
            String ticketTimestamp = CharSequenceUtil.blankToDefault(
                request.getHeader(TicketConstants.HEADER_TICKET_TIMESTAMP), "0");
            // 时间戳转换
            long timestamp = Long.parseLong(ticketTimestamp);
            // 获取调度器
            Long watchamanId = watchmanService.ofSign(watchmanSign).getId();
            // 获取调度器的当前ticket
            String ticket = ticketService.get(watchamanId.toString());
            // 校验ticket
            if (ticketService.verification(watchamanId.toString(),ticket)) {
                // 重新计算签名
                String sign = ticketService.sign(null, timestamp, ticket);
                // 校验成功
                if (!sign.equalsIgnoreCase(ticketSign)) {throw new ValidateException("签名校验失败.\nin:{}\ncalc:{}", ticketSign, sign);}
            } else {
                throw new ValidateException("ticket校验失败.{}", ticket);
            }
        } catch (ValidateException e) {
            result = false;
            exceptionMessage = e.getMessage();
            log.error("验签失败[{}]\n{}", request.getRequestURL().toString(), e.getMessage());
        } catch (RuntimeException e) {
            result = false;
            exceptionMessage = e.getMessage();
            log.error("验签失败[{}]", request.getRequestURL().toString(), e);
        } finally {
            if (Boolean.FALSE.equals(result)) {
                // 设置响应头
                response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
                response.setContentType(ContentType.JSON.getValue());
                response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
                // 设置响应体
                PrintWriter writer = response.getWriter();
                writer.write(jsonService.writeValueAsString(ApiResult.fail(exceptionMessage)));
                writer.flush();
            }
        }
        return result;
    }
}