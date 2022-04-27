package io.shulie.takin.cloud.app.model.callback.basic;

import lombok.Data;

/**
 * 资源实例需要上报的内容
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
public class ResourceExample {
    /**
     * 任务主键
     */
    private long jobId;
    /**
     * 资源主键
     */
    private long resourceId;
    /**
     * 任务实例主键
     */
    private long jobExampleId;
    /**
     * 资源实例主键
     */
    private long resourceExampleId;
}
