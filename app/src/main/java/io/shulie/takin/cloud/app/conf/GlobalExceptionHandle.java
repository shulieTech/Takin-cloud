package io.shulie.takin.cloud.app.conf;

import lombok.extern.slf4j.Slf4j;

import io.shulie.takin.cloud.model.response.ApiResult;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常捕获
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandle {

    @javax.annotation.Resource
    javax.servlet.http.HttpServletRequest httpServletRequest;

    @ExceptionHandler(Exception.class)
    public ApiResult<?> bindExceptionErrorHandler(Exception e) {
        ApiResult<?> apiResult = ApiResult.fail(e.getMessage());
        if (e instanceof NullPointerException) {
            apiResult = ApiResult.fail("空指针");
            log.error("全局异常捕获-空指针.\n", e);
        } else if (e instanceof org.springframework.web.bind.MissingServletRequestParameterException) {
            org.springframework.web.bind.MissingServletRequestParameterException exception = (org.springframework.web.bind.MissingServletRequestParameterException)e;
            apiResult = ApiResult.fail("参数缺失-(" + exception.getParameterName() + ":" + exception.getParameterType() + ")");
            log.error("全局异常捕获-参数缺失.\n请求路径-({})", httpServletRequest.getRequestURL().toString());
        } else {
            log.error("全局异常捕获.\n", e);
        }
        return apiResult;
    }
}