package io.shulie.takin.cloud.sdk;

import java.util.Objects;

import cn.hutool.json.JSONUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.exceptions.ValidateException;

import io.shulie.takin.cloud.constant.Api;
import io.shulie.takin.cloud.sdk.api.ApiRequest;
import io.shulie.takin.cloud.sdk.api.ApiResponse;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.constant.enums.CommandType;
import io.shulie.takin.cloud.model.response.command.CommandContent;

/**
 * Api套件
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@SuppressWarnings("unused")
public class ApiKit {
    private final Api api;
    private final String sign;
    private final String baseUrl;
    private final boolean drillingTicketCheck;

    public ApiKit(String baseUrl, String sign, io.shulie.takin.cloud.constant.Api api) {
        this.api = api;
        this.sign = sign;
        this.baseUrl = baseUrl;
        this.drillingTicketCheck = false;
    }

    public ApiKit(String baseUrl, String sign, io.shulie.takin.cloud.constant.Api api, boolean drillingTicketCheck) {
        this.api = api;
        this.sign = sign;
        this.baseUrl = baseUrl;
        this.drillingTicketCheck = drillingTicketCheck;
    }
    /**
     * 弹出命令
     *
     * @param type   命令类型
     * @param ticket -
     * @return 命令内容
     * @throws ValidateException 接口出错
     */
    public String popCommand(CommandType type, String ticket) throws ValidateException {
        // 组装请求参数
        String query = CharSequenceUtil.format("sign={}&type={}", sign, type.getValue());
        // 请求接口
        ApiResult<CommandContent> apiResult = null;
        try (ApiResponse response = ApiRequest.get(sign, baseUrl, api.getNotify().getCommand().pop(), query)
            .ticket(ticket).execute()) {
            String body = response.body();
            apiResult = JSONUtil.toBean(body, new TypeReference<ApiResult<CommandContent>>() {}, false);
            // 方法在返回数据前进行一下签名校验
            if (drillingTicketCheck){
                response.ticket();
            }
            if (apiResult.isSuccess()) {
                // 返回命令内容的json字符串
                if (Objects.nonNull(apiResult.getData())) {
                    return JSONUtil.toJsonStr(apiResult.getData().getContent());
                }
                // 返回null
                else {return "null";}
            }
            // 抛出接口的异常信息
            else {throw new ValidateException(apiResult.getMsg());}
        } catch (ValidateException e) {
            // 接口响应失败
            if (apiResult != null) {throw new ValidateException(apiResult.getMsg());}
            // 验签失败
            else {throw new ValidateException("验签失败", e);}
        }
    }
}
