package io.shulie.takin.cloud.app.conf;

import java.util.Iterator;
import java.util.Optional;

import cn.hutool.core.text.CharPool;
import cn.hutool.core.text.CharSequenceUtil;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.cloud.model.response.ApiResult;

import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常捕获
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@lombok.extern.slf4j.Slf4j
@org.springframework.web.bind.annotation.RestControllerAdvice
public class GlobalExceptionHandle {

    @javax.annotation.Resource
    javax.servlet.http.HttpServletRequest httpServletRequest;

    @ExceptionHandler(Exception.class)
    public ApiResult<Object> bindExceptionErrorHandler(Exception e) {
        log.error("全局异常捕获.\n", e);
        return ApiResult.fail(e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ApiResult<Object> exceptionHandler(NullPointerException e) {
        log.error("全局异常捕获-空指针.\n", e);
        return ApiResult.fail("空指针");
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ApiResult<Object> exceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException e) {
        log.error("全局异常捕获-消息不可读异常.\n请求路径-({})", httpServletRequest.getRequestURL().toString());
        // 消息处理,取冒号之前的内容
        StringBuilder sb = new StringBuilder()
                .append("message = ")
                .append(e.getMessage())
                .append(" cause = ")
                .append(null == e.getCause() ? "" : e.getCause().toString())
                .append(" StackTrace = ").append(null == e.getStackTrace() || e.getStackTrace().length == 0 ? "" : JSON.toJSONString(e.getStackTrace()));
        log.error("全局异常捕获-消息不可读异常,{}",sb.toString());
        String message = CharSequenceUtil.subBefore(sb.toString(), CharPool.COLON, false);


        return ApiResult.fail(message);
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ApiResult<Object> exceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException e) {
        log.error("全局异常捕获-参数缺失.\n请求路径-({})", httpServletRequest.getRequestURL().toString());
        return ApiResult.fail("参数缺失-(" + e.getParameterName() + ":" + e.getParameterType() + ")");
    }

    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    public ApiResult<Object> exceptionHandler(javax.validation.ConstraintViolationException e) {
        Iterator<String> collect = e.getConstraintViolations().stream()
            .map(javax.validation.ConstraintViolation::getMessage).iterator();
        return ApiResult.fail(CharSequenceUtil.join(",", collect));
    }

    @ExceptionHandler(org.springframework.validation.BindException.class)
    public ApiResult<Object> exceptionHandler(org.springframework.validation.BindException e) {
        Iterator<String> collect = e.getBindingResult().getFieldErrors().stream()
            .map(t -> t.getField() + ":" + t.getDefaultMessage()).iterator();
        return ApiResult.fail(CharSequenceUtil.join(",", collect));
    }
}