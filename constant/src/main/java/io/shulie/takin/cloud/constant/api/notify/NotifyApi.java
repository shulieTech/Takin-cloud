package io.shulie.takin.cloud.constant.api.notify;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;
import io.shulie.takin.cloud.constant.api.notify.pressure.NotifyPressureApi;
import io.shulie.takin.cloud.constant.api.notify.resource.NotifyResourceApi;

/**
 * 调度上报API
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@lombok.Getter
public class NotifyApi {
    @lombok.Getter(lombok.AccessLevel.NONE)
    private final String prefix;

    public NotifyApi(String prefix) {
        this.prefix = prefix;
        this.file = new File(this.getModule());
        this.script = new Script(this.getModule());
        this.command = new Command(this.getModule());
        this.watchman = new Watchman(this.getModule());
        this.pressure = new NotifyPressureApi(this.getModule());
        this.resource = new NotifyResourceApi(this.getModule());
    }

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "notify");}

    /**
     * 文件下
     */
    private final File file;
    /**
     * 脚本
     */
    private final Script script;
    /**
     * 命令
     */
    private final Command command;
    /**
     * 调度机
     */
    private final Watchman watchman;
    /**
     * 资源
     */
    private final NotifyResourceApi resource;
    /**
     * 施压
     */
    private final NotifyPressureApi pressure;
}
