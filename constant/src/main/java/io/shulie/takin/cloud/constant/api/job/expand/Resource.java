package io.shulie.takin.cloud.constant.api.job.expand;

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

    private String getModule() {
        return CharSequenceUtil.join(StrPool.SLASH, prefix, "resource");
    }

    /**
     * 实例明细
     */
    public String exampleList() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "example", "list"), StrPool.SLASH);
    }

    /**
     * 校验
     */
    public String check() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "check"), StrPool.SLASH);
    }
}
