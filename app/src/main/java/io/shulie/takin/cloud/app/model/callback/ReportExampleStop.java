package io.shulie.takin.cloud.app.model.callback;

import io.shulie.takin.cloud.app.model.callback.basic.Basic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.app.model.callback.basic.ReportExample;

/**
 * 资源实例心跳
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportExampleStop extends Basic<ReportExample> {

    private final CallbackType type = CallbackType.RESOURCE_EXAMPLE_STOP;
}
