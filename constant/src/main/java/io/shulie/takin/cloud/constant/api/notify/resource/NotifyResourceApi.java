package io.shulie.takin.cloud.constant.api.notify.resource;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 资源
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@lombok.Getter
public class NotifyResourceApi {
    @lombok.Getter(lombok.AccessLevel.NONE)
    private final String prefix;

    public NotifyResourceApi(String prefix) {
        this.prefix = prefix;
        this.example = new Example(this.getModule());
    }

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "job", "resource");}

    /**
     * 资源实例
     */
    private final Example example;
}
