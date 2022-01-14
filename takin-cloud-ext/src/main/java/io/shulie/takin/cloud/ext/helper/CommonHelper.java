package io.shulie.takin.cloud.ext.helper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author liyuanba
 * @date 2021/11/3 1:42 下午
 */
public class CommonHelper {
    /**
     * 取值选择器
     *
     * @param defValue 默认值
     * @param t        取值对象
     * @param func     取值方法，从对象中取值的方法
     * @param <T>      取值对象类型
     * @param <R>      返回值对象类型
     */
    public static <T, R> R getValue(R defValue, T t, Function<T, R> func) {
        R result = defValue;
        if (null != t) {
            R r = func.apply(t);
            if (null != r) {
                if (r instanceof String) {
                    if (StringUtils.isNotBlank((String)r)) {
                        result = r;
                    }
                } else if (r instanceof List) {
                    if (CollectionUtils.isNotEmpty((List<?>)r)) {
                        result = r;
                    }
                } else if (r instanceof Map) {
                    if (MapUtils.isNotEmpty((Map<?, ?>)r)) {
                        result = r;
                    }
                } else {
                    result = r;
                }
            }
        }
        return result;
    }
    /**
     * 拼装url
     */
    public static String mergeUrl(String domain, String path) {
        return mergePath(domain, path, "/");
    }

    /**
     * 拼装文件目录路径
     */
    public static String mergeDirPath(String dir, String path) {
        return mergePath(dir, path, File.separator);
    }

    public static String mergePath(String path1, String path2, String split) {
        if (path1.endsWith(split)) {
            path1 = path1.substring(0, path1.length() - 1);
        }
        if (!path2.startsWith(split)) {
            path2 = split + path2;
        }
        return path1 + path2;
    }
}
