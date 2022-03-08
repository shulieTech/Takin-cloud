package io.shulie.takin.app.filter;

/**
 * @author pnz.zhao
 * @Description:
 * @date 2022/3/2 17:17
 */
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
@Order(0)
public class CachingRequestBodyFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        if(StringUtils.startsWithIgnoreCase(servletRequest.getContentType(), "multipart/")) {
            chain.doFilter(servletRequest, servletResponse);
        }else {
            HttpServletRequest currentRequest = (HttpServletRequest) servletRequest;
            MultiReadHttpServletRequest requestWrapper = new MultiReadHttpServletRequest(currentRequest);
            chain.doFilter(requestWrapper, servletResponse);
        }
    }
}