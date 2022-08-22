package io.shulie.takin.cloud.app.conf;

/**
 * MyBatis的日志输出实现
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@lombok.extern.slf4j.Slf4j(topic = "MY-BATIS")
public class MyBatisLogger implements org.apache.ibatis.logging.Log {
    public MyBatisLogger(String mark) {
        log.info("初始化MyBatis的日志输出实现:{}", mark);
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    public void error(String s, Throwable e) {
        log.error(s, e);
    }

    @Override
    public void error(String s) {
        log.error(s);
    }

    @Override
    public void debug(String s) {
        log.debug(s);
    }

    @Override
    public void trace(String s) {
        log.trace(s);
    }

    @Override
    public void warn(String s) {
        log.warn(s);
    }
}
