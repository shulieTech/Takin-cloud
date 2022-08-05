package io.shulie.takin.cloud.model.callback;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.model.callback.basic.Base;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.model.callback.basic.PressureExample;

/**
 * 施压任务实例停止
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PressureExampleStop extends Base<PressureExample> {
    private CallbackType type = CallbackType.PRESSURE_EXAMPLE_STOP;
}
