package io.shulie.takin.cloud.app.model.callback;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.app.model.callback.basic.Basic;
import io.shulie.takin.cloud.app.model.callback.basic.JobExample;

/**
 * 任务实例停止
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JobExampleStop extends Basic<JobExample> {
    private final CallbackType type = CallbackType.JMETER_STOP;
}
