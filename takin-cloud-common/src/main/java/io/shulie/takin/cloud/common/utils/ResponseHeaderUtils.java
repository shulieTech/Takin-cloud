package io.shulie.takin.cloud.common.utils;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Author: mubai
 * @Date: 2021-01-11 20:49
 * @Description:
 */
public class ResponseHeaderUtils {

    //原本调用方式，需要将token放入header中
    public static final String PAGE_TOTAL_HEADER = "x-total-count";

    public static void setTotalCount(Long totalCount){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.setHeader("Access-Control-Expose-Headers", PAGE_TOTAL_HEADER);
        if (totalCount !=null){
            response.setHeader(PAGE_TOTAL_HEADER, totalCount + "");
        }else {
            response.setHeader(PAGE_TOTAL_HEADER, 0 + "");
        }

    }
}
