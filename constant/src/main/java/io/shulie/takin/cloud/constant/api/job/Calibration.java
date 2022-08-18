package io.shulie.takin.cloud.constant.api.job;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 数据校准
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class Calibration {
    private final String prefix;

    public Calibration(String prefix) {this.prefix = prefix;}

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "calibration");}

    /**
     * 下发命令
     */
    public String announce() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "announce"), StrPool.SLASH);
    }
}
