package io.shulie.takin.cloud.constant.api;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * Ticket
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@lombok.Getter
public class Ticket {
    @lombok.Getter(lombok.AccessLevel.NONE)
    private final String prefix;

    public Ticket(String prefix) {this.prefix = prefix;}

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "ticket");}

    /**
     * 生成
     */
    public String generate (){return  CharSequenceUtil.addPrefixIfNot(
        CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "generate"), StrPool.SLASH);}
    /**
     * 更新
     */
    public String update (){return  CharSequenceUtil.addPrefixIfNot(
        CharSequenceUtil.join(StrPool.SLASH, this.getModule(), "update"), StrPool.SLASH);}
}
