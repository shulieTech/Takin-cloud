package io.shulie.takin.cloud.constant.api.notify;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 命令
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class Command {
    private final String prefix;

    public Command(String prefix) {this.prefix = prefix;}

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "command");}

    /**
     * ACK
     */
    public String ack() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "ack"), StrPool.SLASH);
    }

    /**
     * POP一条命令
     * <p>自动ACK</p>
     */
    public String pop() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "pop"), StrPool.SLASH);
    }
}
