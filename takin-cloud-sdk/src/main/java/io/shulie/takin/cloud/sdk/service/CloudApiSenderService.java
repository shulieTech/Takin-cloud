package io.shulie.takin.cloud.sdk.service;

import com.alibaba.fastjson.TypeReference;

/**
 * Cloud接口统一发送服务 - 接口
 *
 * @author 张天赐
 */
public interface CloudApiSenderService {

    /**
     * 调用CLOUD接口的统一方法-GET
     *
     * @param <T>           响应类型
     * @param url           请求路径
     * @param responseClass 响应类型
     * @return CLOUD接口响应
     */
    <T> T get(String url, TypeReference<T> responseClass);

    /**
     * 调用CLOUD接口的统一方法-GET
     *
     * @param <T>           响应类型
     * @param url           请求路径
     * @param responseClass 响应类型
     * @return CLOUD接口响应
     */
    <T> T get(String url, Object param, TypeReference<T> responseClass);

    /**
     * 调用CLOUD接口的统一方法-POST
     *
     * @param url           请求路径
     * @param request       请求参数
     * @param responseClass 响应类型
     * @param <T>           响应类型
     * @return CLOUD接口响应
     */
    <T> T post(String url, Object request, TypeReference<T> responseClass);

    /**
     * 调用CLOUD接口的统一方法-PUT
     *
     * @param url           请求路径
     * @param request       请求参数
     * @param responseClass 响应类型
     * @param <T>           响应类型
     * @return CLOUD接口响应
     */
    <T> T put(String url, Object request, TypeReference<T> responseClass);

    /**
     * 调用CLOUD接口的统一方法-DELETE
     *
     * @param url           请求路径
     * @param request       请求参数
     * @param responseClass 响应类型
     * @param <T>           响应类型
     * @return CLOUD接口响应
     */
    <T> T delete(String url, Object request, TypeReference<T> responseClass);
}
