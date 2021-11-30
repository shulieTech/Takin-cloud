package io.shulie.takin.ext.helper;

import java.io.File;

/**
 * @author liyuanba
 * @date 2021/11/3 1:42 下午
 */
public class CommonHelper {
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
