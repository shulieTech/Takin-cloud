package io.shulie.takin.cloud.app.model.notify;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务实例停止
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceExampleError extends Basic<Long> {
    private String message;
}
