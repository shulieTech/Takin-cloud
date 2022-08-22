package io.shulie.takin.cloud.constant.api.job;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 施压
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class Pressure {
    private final String prefix;

    public Pressure(String prefix) {this.prefix = prefix;}

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "pressure");}

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
}
