package io.shulie.takin.cloud.open.api.impl.sender;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.http.Method;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.ContentType;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.net.url.UrlQuery;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import io.shulie.takin.common.beans.response.ResponseResult;

/**
 * Cloud接口统一发送服务
 *
 * @author 张天赐
 */
@Slf4j
@Component
public class CloudApiSenderService {

    @Value("${takin.cloud.url}")
    private String cloudUrl;

    /**
     * 调用AMDB接口的统一方法-GET
     *
     * @param <T>           响应类型
     * @param url           请求路径
     * @param responseClass 响应类型
     * @return AMDB接口响应
     */
    public <T> T get(String url, TypeReference<T> responseClass) {
        // 组装请求路径
        String requestUrl = "";
        try {
            requestUrl = cloudUrl + url;
        } catch (Throwable throwable) {
            log.error("请求Cloud接口失败,GET前置组装失败.\n请求路径:{}.", requestUrl, throwable);
            throw throwable;
        }
        // 发送请求
        return requestApi(Method.GET, requestUrl, new byte[0], responseClass);
    }

    /**
     * 调用AMDB接口的统一方法-GET
     *
     * @param <T>           响应类型
     * @param url           请求路径
     * @param responseClass 响应类型
     * @return AMDB接口响应
     */
    public <T> T get(String url, Object param, TypeReference<T> responseClass) {
        // 组装请求路径
        String requestUrl = "";
        try {
            String urlQuery = UrlQuery.of(BeanUtil.beanToMap(param)).build(StandardCharsets.UTF_8);
            requestUrl = cloudUrl + url + "?" + urlQuery;
        } catch (Throwable throwable) {
            log.error("请求Cloud接口失败,GET前置组装失败.\n请求路径:{}.", requestUrl, throwable);
            throw throwable;
        }
        // 发送请求
        return requestApi(Method.GET, requestUrl, new byte[0], responseClass);
    }

    /**
     * 调用AMDB接口的统一方法-POST
     *
     * @param url           请求路径
     * @param request       请求参数
     * @param responseClass 响应类型
     * @param <T>           响应类型
     * @return AMDB接口响应
     */
    public <T> T post(String url, Object request, TypeReference<T> responseClass) {
        String requestUrl = "";
        String requestBodyString = "";
        byte[] requestBody;
        try {
            // 组装请求路径
            requestUrl = cloudUrl + url;
            // 组装请求体
            if (request == null) {requestBodyString = "";} else {requestBodyString = JSON.toJSONString(request);}
            // 转换请求体
            requestBody = requestBodyString.getBytes(StandardCharsets.UTF_8);
        } catch (Throwable throwable) {
            log.error("请求Cloud接口失败,POST前置转换失败.\n请求路径:{}.请求参数:{}.", requestUrl, requestBodyString, throwable);
            throw throwable;
        }
        // 发送请求
        return requestApi(Method.POST, requestUrl, requestBody, responseClass);
    }

    /**
     * 调用AMDB接口的统一方法-PUT
     *
     * @param url           请求路径
     * @param request       请求参数
     * @param responseClass 响应类型
     * @param <T>           响应类型
     * @return AMDB接口响应
     */
    public <T> T put(String url, Object request, TypeReference<T> responseClass) {
        String requestUrl = "";
        String requestBodyString = "";
        byte[] requestBody;
        try {
            // 组装请求路径
            requestUrl = cloudUrl + url;
            // 组装请求体
            if (request == null) {requestBodyString = "";} else {requestBodyString = JSON.toJSONString(request);}
            // 转换请求体
            requestBody = requestBodyString.getBytes(StandardCharsets.UTF_8);
        } catch (Throwable throwable) {
            log.error("请求Cloud接口失败,POST前置转换失败.\n请求路径:{}.请求参数:{}.", requestUrl, requestBodyString, throwable);
            throw throwable;
        }
        // 发送请求
        return requestApi(Method.PUT, requestUrl, requestBody, responseClass);
    }

    /**
     * 调用AMDB接口的统一方法-DELETE
     *
     * @param url           请求路径
     * @param request       请求参数
     * @param responseClass 响应类型
     * @param <T>           响应类型
     * @return AMDB接口响应
     */
    public <T> T delete(String url, Object request, TypeReference<T> responseClass) {
        String requestUrl = "";
        String requestBodyString = "";
        byte[] requestBody;
        try {
            // 组装请求路径
            requestUrl = cloudUrl + url;
            // 组装请求体
            if (request == null) {requestBodyString = "";} else {requestBodyString = JSON.toJSONString(request);}
            // 转换请求体
            requestBody = requestBodyString.getBytes(StandardCharsets.UTF_8);
        } catch (Throwable throwable) {
            log.error("请求Cloud接口失败,POST前置转换失败.\n请求路径:{}.请求参数:{}.", requestUrl, requestBodyString, throwable);
            throw throwable;
        }
        // 发送请求
        return requestApi(Method.DELETE, requestUrl, requestBody, responseClass);
    }

    /**
     * 调用AMDB接口的统一方法-POST
     *
     * @param method        请求方式
     * @param url           请求路径
     * @param requestBody   请求参数
     * @param responseClass 响应类型
     * @param <T>           响应类型
     * @return AMDB接口响应
     */
    private <T> T requestApi(Method method, String url, byte[] requestBody, TypeReference<T> responseClass) {
        String responseBody = "";
        try {
            // 组装HTTP请求对象
            HttpRequest request = HttpUtil
                .createRequest(method, url)
                .contentType(ContentType.JSON.getValue())
                .headerMap(getDataTrace(), true)
                .body(requestBody);
            // 监控接口耗时
            long startTime = System.currentTimeMillis();
            responseBody = request.execute().body();
            long endTime = System.currentTimeMillis();
            log.info("请求Cloud接口耗时:{}.\n请求路径:{}.\n请求参数:{}.\n请求结果:{}.",
                (endTime - startTime), url, new String(requestBody), responseBody);
            // 返回接口响应
            T apiResponse = JSON.parseObject(responseBody, responseClass);
            if (apiResponse == null) {throw new NullPointerException();}
            if (ResponseResult.class.equals(apiResponse.getClass())) {
                ResponseResult<?> amdbResult = (ResponseResult<?>)apiResponse;
                if (!amdbResult.getSuccess()) {throw new RuntimeException(amdbResult.getError().getMsg());}
            }
            return apiResponse;
        } catch (Throwable throwable) {
            log.error("请求Cloud接口异常.\n请求路径:{}.\n请求参数:{}.\n请求结果:{}.", url, new String(requestBody), responseBody, throwable);
            throw throwable;
        }
    }
    // 填充请求头

    private static final String USER_ID = "user_id";
    private static final String ENV_CODE = "env_code";
    private static final String TENANT_ID = "tenant_id";
    private static final String FILTER_SQL = "filterSql";

    /**
     * 获取请求头信息
     *
     * @return 请求头信息
     */
    private Map<String, String> getDataTrace() {
        ContextExt context = CloudPluginUtils.getContext();
        return new HashMap<String, String>(4) {{
            put(USER_ID, String.valueOf(context.getUserId()));
            put(ENV_CODE, String.valueOf(context.getEnvCode()));
            put(TENANT_ID, String.valueOf(context.getTenantId()));
            put(FILTER_SQL, String.valueOf(context.getFilterSql()));
        }};
    }

}
