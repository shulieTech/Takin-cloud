package io.shulie.takin.cloud.model.callback.basic;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 施压任务实例需要上报的内容
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
public class PressureExample {
    /**
     * 施压任务主键
     */
    private long pressureId;
    /**
     * 资源主键
     */
    private long resourceId;
    /**
     * 施压任务实例主键
     */
    private long pressureExampleId;
    /**
     * 资源实例主键
     */
    private long resourceExampleId;
}
