package io.shulie.takin.cloud.common.test.utils;

import java.util.Date;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.shulie.takin.cloud.common.utils.TestTimeUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * TestTimeUtil 测试类
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
public class TestTimeUtilTester {
    public static void main(String[] args) {
        Date startTime = new Date();
        DateTime endTime = DateUtil.dateNew(startTime)
            .offset(DateField.HOUR, 28)
            .offset(DateField.MINUTE, 59)
            .offset(DateField.SECOND, 59)
            .offset(DateField.MILLISECOND, 999);
        String format = TestTimeUtil.format(startTime, endTime);
        log.info(format);
    }
}
