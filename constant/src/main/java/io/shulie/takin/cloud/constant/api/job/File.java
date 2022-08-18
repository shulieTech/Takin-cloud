package io.shulie.takin.cloud.constant.api.job;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 文件下载
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class File {
    private final String prefix;

    public File(String prefix) {this.prefix = prefix;}

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "file");}

    /**
     * 下发命令
     */
    public String announce() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "announce"), StrPool.SLASH);
    }
}
