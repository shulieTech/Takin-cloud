package io.shulie.takin.cloud.open.api.impl.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;

/**
 * api 调用, 切入点注解类
 *
 * @author liuchuan
 * @date 2021/4/25 10:43 上午
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiPointCut {

    /**
     * 切入点的名称
     *
     * @return 切入点的名称
     */
    String name();

    /**
     * 错误码
     * 由使用方提供
     *
     * @return 错误码
     */
    TakinCloudExceptionEnum errorCode();

}
