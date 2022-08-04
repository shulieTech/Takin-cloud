package io.shulie.takin.cloud.constant;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

/**
 * JMeter插件常量
 * <p>源文件创建时间:2022-06-20 11:02</p>
 *
 * @author chenhongqiao@shulie.com
 */
public class JmeterPluginsConstant {
    private JmeterPluginsConstant() {}

    private static final Map<String, File> LOCAL_PLUGIN_FILES = new HashMap<>();

    public static Map<String, File> getFiles() {
        return LOCAL_PLUGIN_FILES;
    }
}
