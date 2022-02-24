package io.shulie.takin.cloud.common.test.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import lombok.extern.slf4j.Slf4j;

import io.shulie.takin.cloud.common.utils.JmxUtil;

/**
 * {@link JmxUtil}测试类
 *
 * @author 张天赐
 */
@Slf4j
public class JmxUtilTester {
    public static void main(String[] args) {
        String filePath = "/Users/allen/测试计划.jmx";
        log.info("{}",
            JSONObject.toJSONString(
                JmxUtil.buildNodeTree(filePath),
                SerializerFeature.PrettyFormat));
    }
}
