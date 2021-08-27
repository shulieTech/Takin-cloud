package io.shulie.takin.cloud.open.api.impl.aop;

import cn.hutool.json.JSONUtil;
import io.shulie.takin.cloud.open.api.impl.aop.annotation.ApiPointCut;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.http.TakinResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * api 调用 aop
 *
 * @author liuchuan
 * @date 2021/4/25 10:39 上午
 */
@Aspect
@Component
@Slf4j
public class ApiAspect {

    @Around("@annotation(apiPointCut)")
    public Object around(ProceedingJoinPoint joinPoint, ApiPointCut apiPointCut) {
        // 记录入参日志
        log.info("cloud api 调用 --> 切点: {} --> {}", apiPointCut.name(), joinPoint.getSignature());
        log.info("cloud api 调用 --> 入参: {}", JSONUtil.toJsonStr(joinPoint.getArgs()));

        // 执行方法, 熔断等
        log.info("cloud api 调用 --> 方法执行开始");
        ResponseResult<Object> response;
        try {
            Object proceedResult = joinPoint.proceed();
            log.info("cloud api 调用 --> 调用完成, 返回数据: {}", JSONUtil.toJsonStr(proceedResult));
            if (proceedResult == null) {
                response = ResponseResult.fail(apiPointCut.errorCode().getErrorCode(),
                    apiPointCut.errorCode().getDefaultValue());
            } else {
                TakinResponseEntity<ResponseResult<Object>> responseEntity = (TakinResponseEntity<ResponseResult<Object>>)proceedResult;
                if (responseEntity.getSuccess()) {
                    response = responseEntity.getBody();
                } else {
                    response = ResponseResult.fail(responseEntity.getHttpStatus().toString(),
                        responseEntity.getErrorMsg(), "查看cloud日志");
                }
            }

        } catch (Throwable throwable) {
            log.info("cloud api 调用 --> 发生错误 --> {}", throwable.getMessage(), throwable);
            // 熔断
            response = ResponseResult.fail(apiPointCut.errorCode().getErrorCode(),
                apiPointCut.errorCode().getDefaultValue());
        }

        // 记录出参日志
        log.info("cloud api 调用 --> 增强结束!");
        log.info("cloud api 调用 --> 出参: {}", JSONUtil.toJsonStr(response));
        return response;
    }

}
