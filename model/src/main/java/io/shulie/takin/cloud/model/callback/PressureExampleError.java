package io.shulie.takin.cloud.model.callback;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.model.callback.basic.Base;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.model.callback.basic.PressureExample;
import io.shulie.takin.cloud.model.callback.PressureExampleError.PressureExampleErrorInfo;

/**
 * 施压任务实例异常
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PressureExampleError extends Base<PressureExampleErrorInfo> {

    private CallbackType type = CallbackType.PRESSURE_EXAMPLE_ERROR;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class PressureExampleErrorInfo extends PressureExample {
        private String errorMessage;

        public PressureExampleErrorInfo(PressureExample pressureExample) {
            setPressureId(pressureExample.getPressureId());
            setResourceId(pressureExample.getResourceId());
            setPressureExampleId(pressureExample.getPressureExampleId());
            setResourceExampleId(pressureExample.getResourceExampleId());
        }
    }
}
