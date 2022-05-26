package io.shulie.takin.cloud.app.conf;

import lombok.extern.slf4j.Slf4j;

import io.shulie.takin.cloud.model.response.ApiResult;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public ApiResult<Object> bindExceptionErrorHandler(Exception e) {
        ApiResult<Object> apiResult = ApiResult.fail(e.getMessage());
        if (e instanceof NullPointerException) {
            apiResult = ApiResult.fail("空指针");
            log.error("全局异常捕获-空指针.\n", e);
        } else if (e instanceof org.springframework.web.bind.MissingServletRequestParameterException) {
            org.springframework.web.bind.MissingServletRequestParameterException exception = (org.springframework.web.bind.MissingServletRequestParameterException)e;
            apiResult = ApiResult.fail("参数缺失-(" + exception.getParameterName() + ":" + exception.getParameterType() + ")");
            log.error("全局异常捕获-参数缺失.\n请求路径-({})", httpServletRequest.getRequestURL().toString());
        } else if (e instanceof org.springframework.http.converter.HttpMessageNotReadableException) {
            log.error("全局异常捕获-消息不可读异常.\n请求路径-({})", httpServletRequest.getRequestURL().toString());
        } else {
            log.error("全局异常捕获.\n", e);
        }
        return apiResult;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult<Object> constraintViolationExceptionHandler(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> collect = constraintViolations.stream()
                .map(o -> o.getMessage())
                .collect(Collectors.toList());
        return ApiResult.fail(StringUtils.join(collect,","));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Object> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = fieldErrors.stream()
                .map(o -> o.getDefaultMessage())
                .collect(Collectors.toList());
        return ApiResult.fail(StringUtils.join(collect,","));
    }

    @ExceptionHandler(BindException.class)
    public ApiResult<Object> bindExceptionHandler(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = fieldErrors.stream()
                .map(o -> o.getDefaultMessage())
                .collect(Collectors.toList());
        return ApiResult.fail(StringUtils.join(collect,","));
    }
}