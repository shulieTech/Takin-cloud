package io.shulie.takin.cloud.constant.api.notify.pressure;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 资源用量
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class Usage {
    private final String prefix;

    public Usage(String prefix) {this.prefix = prefix;}

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "usage");}

    /**
     * 上报文件用量
     */
    public String uploadFiel() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "upload/file"), StrPool.SLASH);
    }
}
