package io.shulie.takin.cloud.common.constants;

/**
 * 锁 常量
 *
 * @author liuchuan
 * @date 2021/6/2 2:02 下午
 */
public interface LockKeyConstants {

    /**
     * 锁, key 前缀
     */
    String LOCK_PREFIX = "LOCK:";

    /**
     * 导入中间件锁
     * 锁住当前登录用户
     */
    String LOCK_IMPORT_MIDDLEWARE_JAR = String.format("%s%s", LOCK_PREFIX, "IMPORT_MIDDLEWARE:%d");

    /**
     * 中间件比对锁
     * 锁住当前登录用户
     */
    String LOCK_COMPARE_MIDDLEWARE_JAR = String.format("%s%s", LOCK_PREFIX, "COMPARE_MIDDLEWARE:%d");

}
