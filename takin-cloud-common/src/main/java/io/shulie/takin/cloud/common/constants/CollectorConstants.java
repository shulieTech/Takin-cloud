package io.shulie.takin.cloud.common.constants;

/**
 * 常量
 *
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-20 15:55
 */
public class CollectorConstants {

    /**
     * redis key前缀
     */
    public static final String REDIS_PRESSURE_TASK_KEY = "COLLECTOR:JOB:TASK:%s";

    /**
     * 窗口大小
     * <p>
     *     <span>* 动态方案</span>
     *     <ul>
     *         <li>n:时间戳</li>
     *         <li>x:窗口大小</li>
     *     </ul>
     *     <hr/>
     *     <strong>以7作为窗口大小为例:</strong>
     *     <ul>
     *         <li>[00,07)=0</li>
     *         <li>[07,14)=7</li>
     *         <li>[56,03)=56</li>
     *         <li>[03,10)=063(第二分钟)</li>
     *         <li>[06,13)=126(第三分钟)</li>
     *         <li>[02,09)=182(第四分钟)</li>
     *     </ul>
     *     <hr/>
     *     <strong>公式:</strong>{@code (~~(n/x)*x)+((n%x)>=0?0:-x)}
     * </p>
     */
    public static int[] timeWindow = new int[] {0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60};

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
