package io.shulie.takin.cloud.model.callback;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.model.callback.basic.Base;
import io.shulie.takin.cloud.constant.enums.CallbackType;

/**
 * 文件用量上报
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileUsage extends Base<Object> {
    private CallbackType type = CallbackType.FILE_USAGE;
}
