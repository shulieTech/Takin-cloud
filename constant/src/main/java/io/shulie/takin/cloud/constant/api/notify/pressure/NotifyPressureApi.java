package io.shulie.takin.cloud.constant.api.notify.pressure;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 施压
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@lombok.Getter
public class NotifyPressureApi {
    @lombok.Getter(lombok.AccessLevel.NONE)
    private final String prefix;

    public NotifyPressureApi(String prefix) {
        this.prefix = prefix;
        this.usage = new Usage(this.getModule());
        this.example = new Example(this.getModule());
        this.metrics = new Metrics(this.getModule());
    }

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "job", "pressure");}

    /**
     * 资源用量
     */
    private final Usage usage;
    /**
     * 施压实例
     */
    private final Example example;
    /**
     * 指标
     */
    private final Metrics metrics;

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
