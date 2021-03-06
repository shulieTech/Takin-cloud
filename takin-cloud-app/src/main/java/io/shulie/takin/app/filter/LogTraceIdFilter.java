package io.shulie.takin.app.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.MDC;

/**
 * @author shiyajian
 * create: 2020-09-18
 */
public class LogTraceIdFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        MDC.put("traceId", UUID.randomUUID().toString());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
