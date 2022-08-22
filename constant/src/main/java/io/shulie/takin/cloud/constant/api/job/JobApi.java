package io.shulie.takin.cloud.constant.api.job;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.CharSequenceUtil;

import io.shulie.takin.cloud.constant.api.job.expand.JobExpandApi;

/**
 * 任务API
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@lombok.Getter
public class JobApi {
    @lombok.Getter(lombok.AccessLevel.NONE)
    private final String prefix;

    public JobApi(String prefix) {
        this.prefix = prefix;
        this.file = new File(this.getModule());
        this.script = new Script(this.getModule());
        this.pressure = new Pressure(this.getModule());
        this.resource = new Resource(this.getModule());
        this.expand = new JobExpandApi(this.getModule());
        this.calibration = new Calibration(this.getModule());
    }

    private String getModule() {return CharSequenceUtil.join(StrPool.SLASH, prefix, "job");}

    /**
     * 文件下载
     */
    private final File file;
    /**
     * 脚本
     */
    private final Script script;
    /**
     * 施压
     */
    private final Pressure pressure;
    /**
     * 资源
     */
    private final Resource resource;
    /**
     * 数据校准
     */
    private final Calibration calibration;
    /**
     * 拓展
     */
    public final JobExpandApi expand;
}
