package io.shulie.takin.cloud.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.common.beans.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mubai
 * @date 2020-05-26 10:35
 */
@Slf4j
public class HttpUtils {
    public static void writeResponse(HttpServletResponse response, String code, ResponseResult result,
        InputStream inputStream) throws IOException {
        PrintWriter writer = null;
        try {
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("type", "opaqueredirect");
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            writer = response.getWriter();
            writer.write(JSON.toJSONString(result));
            writer.flush();
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：HTTP方法执行异常 --> 异常信息: {}",
                TakinCloudExceptionEnum.HTTP_CMD_EXECUTE_ERROR, e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }
}
