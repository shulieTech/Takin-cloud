package io.shulie.takin.cloud.constant;

/**
 * 异步回调事件
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class Event {

    // 调度 -> Cloud

    /**
     * 调度器 - 注册
     */
    public final static String WATCHMAN_REGISTER = Terminology.WATCHMAN + "_REGISTER";
    /**
     * 调度器 - 心跳
     */
    public final static String WATCHMAN_HEARTBEAT = Terminology.WATCHMAN + "_HEARTBEAT";
    /**
     * 工作空间 - 心跳
     */
    public final static String RESOUECE_HEARTBEAT = Terminology.OFFICE + "_HEARTBEAT";
    /**
     * 工作空间 - 释放
     */
    public final static String RESOUECE_RELEASED = Terminology.OFFICE + "_RELEASED";
    /**
     * 命令 - 确认
     */
    public final static String COMMAND_ACK = "COMMAND_ACK";

    // Cloud -> 第三方

    /**
     * 任务 - 发生提醒
     * <p>SLA 警告/熔断</p>
     */
    public final static String JOB_TIPS = Terminology.JOB + "_TIPS";

    // 通用

    /**
     * 任务 - 已完成
     */
    public final static String JOB_STARTED = Terminology.JOB + "_started";
    /**
     * 任务 - 已结束
     */
    public final static String JOB_ENDED = Terminology.JOB + "_ended";
}
