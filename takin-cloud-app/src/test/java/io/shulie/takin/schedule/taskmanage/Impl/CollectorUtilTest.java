package io.shulie.takin.schedule.taskmanage.Impl;

import java.util.Calendar;

import io.shulie.takin.cloud.common.utils.CollectorUtil;
import org.junit.Test;

/**
 * @author 无涯
 * @date 2020/10/28 9:40 上午
 */
public class CollectorUtilTest {
    @Test
    public void test() {
        long time = 1626666689000L;
        Calendar calendar = CollectorUtil.getTimeWindow(time);
        System.out.println(calendar.getTimeInMillis());
    }

}
