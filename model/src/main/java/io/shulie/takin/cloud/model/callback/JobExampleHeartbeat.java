package io.shulie.takin.cloud.model.callback;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.model.callback.basic.Base;
import io.shulie.takin.cloud.model.callback.basic.JobExample;

/**
 * 任务实例心跳
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JobExampleHeartbeat extends Base<JobExample> {
    private CallbackType type = CallbackType.JOB_EXAMPLE_HEARTBEAT;
}
