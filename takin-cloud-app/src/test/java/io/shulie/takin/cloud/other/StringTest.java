package io.shulie.takin.cloud.other;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author liuchuan
 * @date 2021/6/2 10:40 上午
 */
@Slf4j
public class StringTest {

    @Test
    public void testIndexOf() {
        String version = "1.0.3.1";
        log.info(version.substring(0, version.indexOf(".", 1)));
    }

}
