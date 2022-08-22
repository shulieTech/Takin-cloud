package io.shulie.takin.cloud.sdk.api;

import java.io.Closeable;

import lombok.Getter;

import cn.hutool.http.HttpResponse;
import cn.hutool.core.exceptions.ValidateException;

import io.shulie.takin.cloud.sdk.TicketUtils;
import io.shulie.takin.cloud.constant.TicketConstants;

/**
 * Api响应
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@SuppressWarnings("unused")
public class ApiResponse implements Closeable {
    @Getter
    private final ApiRequest apiRequest;
    @Getter
    private final HttpResponse httpResponse;

    protected ApiResponse(HttpResponse httpResponse, ApiRequest apiRequest) {
        this.apiRequest = apiRequest;
        this.httpResponse = httpResponse;
    }

    public String body() {
        return httpResponse.body();
    }

    /**
     * 验签
     *
     * @throws ValidateException 验签失败
     */
    public void ticket() throws ValidateException {
        String responseTime = httpResponse.header(TicketConstants.HEADER_TICKET_TIMESTAMP);
        String responseSign = httpResponse.header(TicketConstants.HEADER_TICKET_SIGN);
        if (responseTime == null || responseSign == null) {
            throw new ValidateException("响应请求头中未获取到签名相关信息");
        }
        String trimSign = TicketUtils.sign(Long.parseLong(responseTime), apiRequest.getTicket());
        if (!responseSign.equals(trimSign)) {throw new ValidateException("签名校验失败");}
    }

    @Override
    public void close() {
        httpResponse.close();
    }
}
