package io.shulie.takin.cloud.constant.api.notify.pressure;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 施压指标
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class Metrics {
    private final String prefix;

    public Metrics(String prefix) {this.prefix = prefix;}

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "metrics");}

    /**
     * 上报数据
     */
    public String upload() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "upload"), StrPool.SLASH);
    }

    /**
     * 上报数据 - 旧方式
     */
    public String oldUpload() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "upload_old"), StrPool.SLASH);
    }
}
