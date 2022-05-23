package io.shulie.takin.app.interceptor;

/**
 * @author pnz.zhao
 * @Description:
 * @date 2022/2/23 14:48
 */
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSON;
import io.shulie.takin.app.filter.MultiReadHttpServletRequest;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.security.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
@Slf4j
public class SecurityInterceptor implements HandlerInterceptor{

    Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);
    // 填充请求头
    private static final String USER_ID = "user-id";
    private static final String ENV_CODE = "env-code";
    private static final String TENANT_ID = "tenant-id";
    private static final String FILTER_SQL = "filterSql";
    private static final String TENANT_CODE = "tenant-code";

    private boolean checkSign(ServletRequest request) throws IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Map<String, String> headMap = new HashMap<>();
        headMap.put(USER_ID, httpRequest.getHeader(USER_ID));
        headMap.put(ENV_CODE, httpRequest.getHeader(ENV_CODE));
        headMap.put(TENANT_ID, httpRequest.getHeader(TENANT_ID));
        headMap.put(FILTER_SQL, httpRequest.getHeader(FILTER_SQL));
        headMap.put(TENANT_CODE, httpRequest.getHeader(TENANT_CODE));
        headMap.put("time", httpRequest.getHeader("time"));
        String clientMd5 = httpRequest.getHeader("md5");
        String url =httpRequest.getRequestURI();
        String queryStr = httpRequest.getQueryString();
        if(StringUtils.isNotBlank(queryStr)){
            url += "?"+queryStr;
        }
        TreeMap<String,String> treeMap = new TreeMap<>();
        treeMap.putAll(headMap);
        treeMap.put("url",url);
        if(!StringUtils.startsWithIgnoreCase(httpRequest.getContentType(), "multipart/")) {
            //文件上传.排出body内容部分
            MultiReadHttpServletRequest requestWrapper = (MultiReadHttpServletRequest) httpRequest;
            treeMap.put("body", requestWrapper._body);
        }else {
            treeMap.put("body", "");
        }
        String signBodyStr = treeMap.toString().replace("null","");
        String serverMd5 = null;
        try {
            serverMd5 = MD5Utils.getInstance().getMD5(signBodyStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean checkFlag;
//        log.info("========security interceptor========");
//        log.info("url:"+url);
        if(StringUtils.isNotBlank(clientMd5)&&StringUtils.isNotBlank(serverMd5)&&
                !clientMd5.equals(serverMd5)){
//            log.info("验签失败");
            checkFlag = false;
        }else{
//            log.info("验签通过");
            checkFlag = true;
        }
        return checkFlag;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
//        logger.info("api securityHandle:" +request.getRequestURI());
        if(request.getRequestURI().contains("/api/")){
            if(checkSign(request)) {
                return true;
            }else{
                response.getWriter().write(JSON.toJSON(ResponseResult.fail("403","signature error!","request must be from takin-web!")).toString());
                return false;
            }
        }else{
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }
}
