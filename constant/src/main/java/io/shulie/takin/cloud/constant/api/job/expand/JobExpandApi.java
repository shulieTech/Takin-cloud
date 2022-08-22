package io.shulie.takin.cloud.constant.api.job.expand;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 拓展任务API
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@lombok.Getter
public class JobExpandApi {
    @lombok.Getter(lombok.AccessLevel.NONE)
    private final String prefix;

    public JobExpandApi(String prefix) {
        this.prefix = prefix;
        this.script = new Script(this.getModule());
        this.pressure = new Pressure(this.getModule());
        this.resource = new Resource(this.getModule());
    }

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "expand");}

    /**
     * 脚本
     */
    private final Script script;
    /**
     * 资源
     */
    private final Resource resource;
    /**
     * 发压
     */
    private final Pressure pressure;
}
