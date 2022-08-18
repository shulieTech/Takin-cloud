package io.shulie.takin.cloud.constant.api.job;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 脚本
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class Script {
    private final String prefix;

    public Script(String prefix) {this.prefix = prefix;}

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "script");}

    /**
     * 下发命令
     */
    public String announce() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "announce"), StrPool.SLASH);
    }
}
