package io.shulie.takin.cloud.constant.api.notify;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 调度器
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class Watchman {
    private final String prefix;

    public Watchman(String prefix) {
        this.prefix = prefix;
    }

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "watchman");}

    /**
     * 心跳
     */
    public String heartbeat() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "heartbeat"), StrPool.SLASH);
    }

    /**
     * 发生异常
     */
    public String abnormal() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "abnormal"), StrPool.SLASH);
    }

    /**
     * 恢复正常
     */
    public String normal() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "normal"), StrPool.SLASH);
    }

    /**
     * 信息上报
     * <p>K8s资源</p>
     */
    public String upload() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "upload"), StrPool.SLASH);
    }
}
