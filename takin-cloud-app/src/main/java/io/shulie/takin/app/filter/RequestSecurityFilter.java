package io.shulie.takin.app.filter;

import com.alibaba.fastjson.JSONObject;
import io.shulie.takin.app.conf.RequestWrapper;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.json.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RequestSecurityFilter implements Filter {

    @Value("${security.request.timeout: 180000}")
    private long timeout = 1000 * 60 * 3L;
    /**
     * 秘钥存储
     */
    @Value("${cloud.request.security.appkeys:}")
    private String requestSecurityAppkeys;

    private static final Map<String, String> appKeySet = new HashMap<>(16);

    @Override
    public void init(FilterConfig filterConfig) {
        appKeySet.put("web_0423_appkey", "cloud_safdseadvdsa");
        if (StringUtils.isNotBlank(requestSecurityAppkeys)) {
            Map<String, String> requestSecurityKeyMap = JsonHelper.json2Map(requestSecurityAppkeys, String.class, String.class);
            appKeySet.putAll(requestSecurityKeyMap);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        //swagger文档相关接口不做拦截
        if (httpServletRequest.getRequestURI().contains("/swagger")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String validateAppkey = httpServletRequest.getHeader("validate-appkey");
        String validateTimestamp = httpServletRequest.getHeader("validate-timestamp");
        String validateSignature = httpServletRequest.getHeader("validate-signature");

        //没有秘钥，不能访问
        if (!appKeySet.containsKey(validateAppkey)) {
            failRequest(servletResponse, "签名公钥错误", "请重新发起请求");
            return;
        }
        try {
            long requestTimestamp = Long.parseLong(validateTimestamp);
            //如果请求发送过来已经超过了超时时间，不能访问
            if (System.currentTimeMillis() - requestTimestamp >= timeout) {
                failRequest(servletResponse, "签名请求超时", "请重新发起请求");
                return;
            }
        } catch (Exception e) {
            //时间格式不对，不能访问
            failRequest(servletResponse, "签名时间格式不对，不能访问", "请使用正确的时间格式");
            return;
        }
        StringBuilder validateKey = new StringBuilder();
        /**
         * 文件导入
         */
        if (servletRequest.getContentType().startsWith("multipart/form-data")) {
            ((HttpServletRequest) servletRequest).getParts().forEach(part -> {
                validateKey.append("file-name=").append(part.getSubmittedFileName()).append("file-size=").append(part.getSize());
            });
            signValidate(servletRequest, servletResponse, filterChain, validateAppkey, validateTimestamp, validateSignature, validateKey);

        } else {
            RequestWrapper requestWrapper = new RequestWrapper((HttpServletRequest) servletRequest);
            if (!CollectionUtils.isEmpty(requestWrapper.getParameterMap())) {
                Map<String, String[]> parameterMap = requestWrapper.getParameterMap();
                String param = JSONObject.toJSONString(parameterMap);
                validateKey.append(param);
            }
            if (StringUtils.isNotBlank(requestWrapper.getBody())) {
                validateKey.append(requestWrapper.getBody());
            }

            signValidate(requestWrapper, servletResponse, filterChain, validateAppkey, validateTimestamp, validateSignature, validateKey);
        }

    }

    private void signValidate(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain,
                              String validateAppkey, String validateTimestamp, String validateSignature,
                              StringBuilder validateKey) throws IOException, ServletException {

        validateKey.append("validate-appkey=").append(validateAppkey).append("validate-timestamp=")
                .append(validateTimestamp);

        String signature = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, appKeySet.get(validateAppkey)).hmacHex(validateKey.toString());
        if (StringUtils.isBlank(signature) || !signature.equals(validateSignature)) {
            //秘钥错误
            failRequest(servletResponse, "签名校验失败", "请检查秘钥是否正确");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    private void failRequest(ServletResponse servletResponse, String errorMsg, String solution) throws IOException {
        ResponseResult<Object> fail = ResponseResult.fail(TakinCloudExceptionEnum.COMMON_VERIFY_ERROR.getErrorCode(),
                errorMsg, solution);
        ServletOutputStream outputStream = servletResponse.getOutputStream();
        outputStream.write(JSONObject.toJSONString(fail).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
