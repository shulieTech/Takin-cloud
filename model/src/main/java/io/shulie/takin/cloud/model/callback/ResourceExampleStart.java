package io.shulie.takin.cloud.model.callback;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.shulie.takin.cloud.model.callback.basic.Base;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.model.callback.basic.ResourceExample;

/**
 * 资源实例启动
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ResourceExampleStart extends Base<ResourceExample> {
    private CallbackType type = CallbackType.RESOURCE_EXAMPLE_START;
}
