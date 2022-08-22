package io.shulie.takin.cloud.constant.api.job.expand;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 发压
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class Pressure {
    private final String prefix;

    public Pressure(String prefix) {this.prefix = prefix;}

    private String getModule() {
        return CharSequenceUtil.join(StrPool.SLASH, prefix, "pressure");
    }

    /**
     * 获取配置
     */
    public String getConfig() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "config", "get"), StrPool.SLASH);
    }

    /**
     * 修改配置
     */
    public String modifyConfig() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "config", "modify"), StrPool.SLASH);
    }
}
