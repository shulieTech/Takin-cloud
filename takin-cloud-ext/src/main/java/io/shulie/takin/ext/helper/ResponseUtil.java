package io.shulie.takin.ext.helper;

import io.shulie.takin.ext.content.response.Response;

/**
 * @Author: liyuanba
 * @Date: 2021/11/5 5:07 下午
 */
public class ResponseUtil {
    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }
    public static <T> Response<T> error(String code, String msg) {
        Response<T> response = new Response<>();
        response.setSuccess(false);
        response.setCode(code);
        response.setMsg(msg);
        return response;
    }
}
