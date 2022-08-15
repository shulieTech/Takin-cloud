package io.shulie.takin.cloud.constant;

/**
 * ticket常量
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class TicketConstants {

    private TicketConstants() {}

    /**
     * 加密签名
     */
    public static final String HEADER_TICKET_SIGN = "TICKET-SIGN";
    /**
     * 调度器标识签名
     */
    public static final String HEADER_WATCHMAN_SIGN = "WATCHMAN-SIGN";
    /**
     * 参与加密的时间戳
     */
    public static final String HEADER_TICKET_TIMESTAMP = "TICKET-TIMESTAMP";
}
