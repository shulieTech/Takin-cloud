package io.shulie.takin.cloud.constant;

import cn.hutool.core.util.StrUtil;

/**
 * 术语
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class Terminology {
    /**
     * Cloud
     * <p>提供压力引擎调度的接口服务</p>
     */
    public final static String CLOUD = "CLOUD";
    /**
     * 调度器
     * <p>压力引擎调度的代理</p>
     */
    public final static String WATCHMAN = "SCHEDULE";
    /**
     * 需要调度的工作内容
     */
    public final static String JOB = "JOB";
    /**
     * 工作区域
     * <p>K8S资源</p>
     * <p>工作内容的运行空间</p>
     */
    public final static String OFFICE = "RESOURCE";
    /**
     * 异步回调
     */
    public final static String CALLBACK = "CALLBACK";
    /**
     * 成功
     * <p>操作结果"成功"的标识</p>
     */
    public final static String SUCCESS = "SUCCESS";
    /**
     * 失败
     * <p>操作结果"失败"的标识</p>
     */
    public final static String ERROR = "ERROR";
}
