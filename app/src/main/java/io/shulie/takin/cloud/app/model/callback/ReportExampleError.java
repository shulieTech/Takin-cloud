package io.shulie.takin.cloud.app.model.callback;

import io.shulie.takin.cloud.app.model.callback.basic.Basic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.app.model.callback.basic.ReportExample;
import io.shulie.takin.cloud.app.model.callback.ReportExampleError.ReportExampleErrorInfo;

/**
 * 资源实例异常
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportExampleError extends Basic<ReportExampleErrorInfo> {

    private final CallbackType type = CallbackType.RESOURCE_EXAMPLE_ERROR;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ReportExampleErrorInfo extends ReportExample {
        private String errorMessage;
    }
}
