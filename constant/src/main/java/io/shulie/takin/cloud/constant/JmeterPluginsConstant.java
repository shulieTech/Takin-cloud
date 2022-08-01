package io.shulie.takin.cloud.constant;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

/**
 * JMeter插件常量
 *
 * @author chenhongqiao@shulie.com
 * @date 2022/6/20   11:02
 */
public class JmeterPluginsConstant {
    private JmeterPluginsConstant() {}

    private static final Map<String, File> LOCAL_PLUGIN_FILES = new HashMap<>();

    public static File put(String key, File value) {
        return LOCAL_PLUGIN_FILES.put(key, value);
    }

    public static Set<Map.Entry<String, File>> entrySet() {
        return LOCAL_PLUGIN_FILES.entrySet();
    }

    public static File getOrDefault(String key, File defaultValue) {
        return LOCAL_PLUGIN_FILES.getOrDefault(key, defaultValue);
    }
}
