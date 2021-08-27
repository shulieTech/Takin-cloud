package io.shulie.takin.app.conf;

import javax.servlet.Filter;

import io.shulie.takin.app.filter.LogTraceIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: vernon
 * @Date: 2019/12/2 15:34
 * @Description:添加put请求过滤器
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean pufFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<Filter>();
        registration.addUrlPatterns("/*");
        registration.setFilter(new org.springframework.web.filter.HttpPutFormContentFilter());
        registration.setName("httpPutFormContentFilter");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<LogTraceIdFilter> logTraceIdFilter() {
        FilterRegistrationBean<LogTraceIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LogTraceIdFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(0);
        registrationBean.setName("logTraceIdFilter");
        return registrationBean;
    }

}
