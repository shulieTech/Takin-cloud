package io.shulie.takin.cloud.constant.api.job;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 资源
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class Resource {
    private final String prefix;

    public Resource(String prefix) {this.prefix = prefix;}

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "resource");}

    /**
     * 锁定
     */
    public String lock() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "lock"), StrPool.SLASH);
    }

    /**
     * 释放
     */
    public String unlock() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "unlock"), StrPool.SLASH);
    }
}
