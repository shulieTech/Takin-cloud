package io.shulie.takin.cloud.constant.api;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 通用
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class Common {
    private final String prefix;

    public Common(String prefix) {
        this.prefix = prefix;
    }

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "common");}

    /**
     * 健康检查
     */
    public String health() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "health", "checkup"), StrPool.SLASH);
    }

    /**
     * 版本信息
     */
    public String version() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "version"), StrPool.SLASH);
    }
}
