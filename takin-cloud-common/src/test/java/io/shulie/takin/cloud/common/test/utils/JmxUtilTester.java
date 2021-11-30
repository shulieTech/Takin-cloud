package io.shulie.takin.cloud.common.test.utils;

import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.cloud.common.utils.JmxUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link JmxUtil}测试类
 *
 * @author 张天赐
 */
@Slf4j
public class JmxUtilTester {
    public static void main(String[] args) {
        log.info("{}",
            JSONObject.toJSONString(JmxUtil.buildNodeTree("/Users/allen/Desktop/脚本解析校验.jmx")));
    }
}
