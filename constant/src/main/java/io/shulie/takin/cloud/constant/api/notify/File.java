package io.shulie.takin.cloud.constant.api.notify;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 文件
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class File {
    private final String prefix;

    public File(String prefix) {this.prefix = prefix;}

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "job", "file");}

    /**
     * 更新进度
     */
    public String updateProgress() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "progress", "update"), StrPool.SLASH);
    }

    /**
     * 更新进度 - 批量
     */
    public String batchUpdateProgress() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, updateProgress(), "batch"), StrPool.SLASH);
    }

    /**
     * 失败
     */
    public String failed() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "failed"), StrPool.SLASH);
    }

}
