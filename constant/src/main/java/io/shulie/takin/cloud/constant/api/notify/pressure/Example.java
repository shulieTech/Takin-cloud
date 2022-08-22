package io.shulie.takin.cloud.constant.api.notify.pressure;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 施压实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class Example {
    private final String prefix;

    public Example(String prefix) {this.prefix = prefix;}

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "example");}

    /**
     * 心跳
     */
    public String heartbeat() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "heartbeat"), StrPool.SLASH);
    }

    /**
     * 启动
     */
    public String start() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "start"), StrPool.SLASH);
    }

    /**
     * 停止
     */
    public String stop() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "stop"), StrPool.SLASH);
    }

    /**
     * 发生异常
     */
    public String error() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "error"), StrPool.SLASH);
    }
}
