package io.shulie.takin.cloud.app.conf.ticket;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * 加签验签拦截器
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Configuration
public class TicketInterceptor implements WebMvcConfigurer {
    @Resource
    TicketInterceptorHandler ticketInterceptorHandler;

    /**
     * {@inheritDoc}
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ticketInterceptorHandler)
            .addPathPatterns("/notify/**");
    }
}
