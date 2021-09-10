package io.shulie.takin.cloud.common.utils;

import java.util.Date;
import java.time.ZoneId;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 压测时长工具类
 *
 * @author xingchen
 * @author 张天赐
 */
public class TestTimeUtil {

    /**
     * 压测时长：
     * 无启动时间，返回null
     * 无停止时间，返回null
     * 停止时间-启动时间
     *
     * @param startTime -
     * @param endTime   -
     * @return -
     */
    public static String format(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) {
            return null;
        }
        LocalDateTime start = startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime end = endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        long seconds = Duration.between(start, end).getSeconds();
        long hour = seconds / 3600;
        long minutes = seconds / 60;
        long second = seconds % 60;
        return String.format("%dh %d'%d\"", hour, minutes, second);
    }
}
