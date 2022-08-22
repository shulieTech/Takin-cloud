package io.shulie.takin.cloud.constant;

import io.shulie.takin.cloud.constant.api.Common;
import io.shulie.takin.cloud.constant.api.Ticket;
import io.shulie.takin.cloud.constant.api.Watchman;
import io.shulie.takin.cloud.constant.api.job.JobApi;
import io.shulie.takin.cloud.constant.api.notify.NotifyApi;

/**
 * API
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@lombok.Getter
public class Api {
    @lombok.Getter(lombok.AccessLevel.NONE)
    private final String prefix;

    public Api(String prefix) {
        this.prefix = prefix;
        this.job = new JobApi(prefix);
        this.common = new Common(prefix);
        this.ticket = new Ticket(prefix);
        this.notify = new NotifyApi(prefix);
        this.watchman = new Watchman(prefix);
    }

    /**
     * 通用接口
     */
    private final Common common;
    /**
     * ticket
     */
    private final Ticket ticket;
    /**
     * 调度机
     */
    private final Watchman watchman;
    /**
     * 任务
     */
    private final JobApi job;
    /**
     * 调度回调
     */
    private final NotifyApi notify;

    /**
     * 静态实例
     */
    public static final Api EMPTY_INSTANCE = new Api("");
}
