package io.shulie.takin.cloud.model.callback;

import io.shulie.takin.cloud.model.callback.basic.JobExample;
import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.model.callback.basic.Basic;

/**
 * 任务实例停止
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JobExampleStop extends Basic<JobExample> {
    private final CallbackType type = CallbackType.JOB_EXAMPLE_STOP;
}
