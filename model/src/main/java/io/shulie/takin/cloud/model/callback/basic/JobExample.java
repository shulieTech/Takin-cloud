package io.shulie.takin.cloud.model.callback.basic;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 任务实例需要上报的内容
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
public class JobExample {
    /**
     * 任务主键
     */
    private long jobId;
    /**
     * 资源主键
     */
    private long resourceId;
    /**
     * 资源实例主键
     */
    private long resourceExampleId;
    /**
     * 任务实例主键
     */
    private long jobExampleId;
}
