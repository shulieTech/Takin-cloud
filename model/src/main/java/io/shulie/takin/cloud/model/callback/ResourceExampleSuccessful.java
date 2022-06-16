package io.shulie.takin.cloud.model.callback;

import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.model.callback.ResourceExampleError.ResourceExampleErrorInfo;
import io.shulie.takin.cloud.model.callback.basic.Base;
import io.shulie.takin.cloud.model.callback.basic.ResourceExample;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资源实例异常
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceExampleSuccessful extends Base<ResourceExample> {
    private CallbackType type = CallbackType.JOB_EXAMPLE_SUCCESSFUL;
}
