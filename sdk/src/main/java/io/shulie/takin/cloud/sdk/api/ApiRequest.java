package io.shulie.takin.cloud.sdk.api;

import java.util.Map;

import lombok.Getter;

import cn.hutool.http.Method;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.net.url.UrlBuilder;

import io.shulie.takin.cloud.sdk.TicketUtils;
import io.shulie.takin.cloud.constant.TicketConstants;

/**
 * Api请求
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@SuppressWarnings("unused")
public class ApiRequest {
    @Getter
    private String ticket;
    private final String sign;
    private final HttpRequest httpRequest;

    public static ApiRequest get(String watchmanSign, String host, String url) {
        UrlBuilder urlBuilder = UrlBuilder.ofHttp(host).addPath(url);
        return new ApiRequest(watchmanSign, urlBuilder.toString(), Method.GET);
    }

    public static ApiRequest get(String watchmanSign, String host, String url, String queryString) {
        UrlQuery query = UrlQuery.of(queryString, CharsetUtil.CHARSET_UTF_8);
        UrlBuilder urlBuilder = UrlBuilder.ofHttp(host).addPath(url).setQuery(query);
        return new ApiRequest(watchmanSign, urlBuilder.toString(), Method.GET);
    }

    public static ApiRequest get(String watchmanSign, String host, String url, Map<String, Object> query) {
        UrlBuilder urlBuilder = UrlBuilder.ofHttp(host).addPath(url).setQuery(new UrlQuery(query));
        return new ApiRequest(watchmanSign, urlBuilder.toString(), Method.GET);
    }

    public static ApiRequest post(String watchmanSign, String host, String url) {
        UrlBuilder urlBuilder = UrlBuilder.ofHttp(host).addPath(url);
        return new ApiRequest(watchmanSign, urlBuilder.toString(), Method.POST);
    }

    private ApiRequest(String watchmanSign, String url, Method method) {
        this.sign = watchmanSign;
        this.httpRequest = HttpUtil.createRequest(method, url);
    }

    /**
     * 加签
     *
     * @param ticket -
     * @return -
     */
    public ApiRequest ticket(String ticket) {
        this.ticket = ticket;
        this.ticket(this.sign, System.currentTimeMillis(), this.ticket);
        return this;
    }

    /**
     * 设置请求体
     *
     * @param body 请求体
     * @return -
     */
    public ApiRequest body(String body) {
        this.httpRequest.body(body);
        return this;
    }

    /**
     * 设置请求体
     * <p>多次调用时,以最后一次调用结果为准</p>
     *
     * @param body        请求体
     * @param contentType Content-Type
     * @return -
     */
    public ApiRequest body(String body, String contentType) {
        this.httpRequest.body(body, contentType);
        return this;
    }

    /**
     * 在请求头中附加ticket
     */
    private void ticket(String sign, long requestTime, String ticket) {
        String secret = TicketUtils.sign(requestTime, ticket);
        this.httpRequest
            .header(TicketConstants.HEADER_TICKET_SIGN, secret)
            .header(TicketConstants.HEADER_WATCHMAN_SIGN, sign)
            .header(TicketConstants.HEADER_TICKET_TIMESTAMP, String.valueOf(requestTime));
    }

    /**
     * 执行api
     *
     * @return 执行结果
     */
    public ApiResponse execute() {
        HttpResponse httpResponse = this.httpRequest.execute();
        return new ApiResponse(httpResponse, this);
    }
}
