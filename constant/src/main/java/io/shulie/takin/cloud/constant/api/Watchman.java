package io.shulie.takin.cloud.constant.api;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 调度器
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@lombok.Getter
public class Watchman {
    @lombok.Getter(lombok.AccessLevel.NONE)
    private final String prefix;

    public Watchman(String prefix) {this.prefix = prefix;}

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "watchman");}

    private static final String BATCH = "batch";

    /**
     * 列表
     */
    public String list() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "list"), StrPool.SLASH);
    }

    /**
     * 状态
     */
    public String status() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "status"), StrPool.SLASH);
    }

    /**
     * 状态 - 批量
     */
    public String batchStatus() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, status(), BATCH), StrPool.SLASH);
    }

    /**
     * 资源
     */
    public String resource() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "resource"), StrPool.SLASH);
    }

    /**
     * 资源 - 批量
     */
    public String batchResource() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, resource(), BATCH), StrPool.SLASH);
    }

    /**
     * 更新
     */
    public String update() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "update"), StrPool.SLASH);
    }

    /**
     * 更新 - 批量
     */
    public String batchUpdate() {
        return CharSequenceUtil.addPrefixIfNot(
            CharSequenceUtil.join(StrPool.SLASH, update(), BATCH), StrPool.SLASH);
    }
}

