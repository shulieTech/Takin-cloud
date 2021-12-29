package io.shulie.takin.cloud.common.test.utils;

import java.io.File;

import cn.hutool.core.io.FileUtil;
import io.shulie.takin.cloud.common.utils.Md5Util;

/**
 * {@link Md5Util} 的测试类
 *
 * @author 张天赐
 */
public class Md5UtilTester {
    public static void main(String[] args) {
        //        String  file = "/Users/liyuanba/Downloads/data 2.csv";
        String file = "/Users/liyuanba/Downloads/data.csv";
        System.out.println("fileSize=" + FileUtil.file(file).length());
        System.out.println("md5=" + Md5Util.md5(file));
        long t = System.currentTimeMillis();
        System.out.println("file md5=" + Md5Util.md5File(file));
        System.out.println("t=" + (System.currentTimeMillis() - t));
    }
}
