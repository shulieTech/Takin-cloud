package io.shulie.takin.cloud.constant;

/**
 * 常量
 * <p>源文件创建时间:2020-04-20 15:55</p>
 *
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 */
public class CollectorConstants {
    private CollectorConstants() {}

    /**
     * redis key前缀
     */
    public static final String REDIS_PRESSURE_TASK_KEY = "COLLECTOR:JOB:TASK:%s";

    /**
     * 指标key 超时时间
     */
    public static final long REDIS_KEY_TIMEOUT = 60;
    /**
     * 单位：秒
     */
    public static final int OVERDUE_SECOND = 10;
    /**
     * 10秒过期策略，超时丢弃Metrics 数据，单位：毫秒
     */
    public static final long OVERDUE_TIME = 2000L * OVERDUE_SECOND;
    /**
     * Metrics 统计时间间隔
     */
    public static final int SEND_TIME = 5;
    public static final int SECOND_60 = 60;

}
