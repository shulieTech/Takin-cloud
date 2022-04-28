package io.shulie.takin.cloud.model.callback;

import io.shulie.takin.cloud.model.callback.basic.Basic;
import io.shulie.takin.cloud.model.callback.basic.ResourceExample;
import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.constant.enums.CallbackType;

/**
 * 资源实例心跳
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceExampleHeartbeat extends Basic<ResourceExample> {

    private final CallbackType type = CallbackType.RESOURCE_EXAMPLE_HEARTBEAT;

}
