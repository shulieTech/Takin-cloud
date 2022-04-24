package io.shulie.takin.cloud.constant;

/**
 * Api地址
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class ApiUrl {
    /**
     * 调度器 - 列表
     */
    public static final String WATCHMAN_LIST = "/" + Terminology.WATCHMAN + "/list";
    /**
     * 调度器资源
     */
    private static final String WATCHMAN_RESOURCE = "/" + Terminology.WATCHMAN + "/resource";
    /**
     * 调度器资源 - 概览
     */
    public static final String WATCHMAN_RESOURCE_OVERVIEW = WATCHMAN_RESOURCE + "/overview";
    /**
     * 调度器资源 - 占有
     */
    public static final String WATCHMAN_RESOURCE_OCCUPY = WATCHMAN_RESOURCE + "/occupy";
    /**
     * 调度器资源 - 释放
     */
    public static final String WATCHMAN_RESOURCE_RELEASE = WATCHMAN_RESOURCE + "/release";
    /**
     * 操作/事件 回调
     */
    public static final String CALLBACK = "/" + Terminology.CALLBACK;

    /**
     * 应用程序健康状态
     */
    public static final String HEALTH = "/" + "health";
    /**
     * 应用程序健康状态 - 健康检查
     */
    public static final String HEALTH_CHECKUP = HEALTH + "/checkUp";
}
