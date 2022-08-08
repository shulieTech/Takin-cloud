package io.shulie.takin.cloud.model.callback.script;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.shulie.takin.cloud.model.callback.basic.Base;
import io.shulie.takin.cloud.constant.enums.CallbackType;

/**
 * 脚本校验的结果上报
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ResultReport extends Base<Long> {
    /**
     * 回调类型
     */
    private CallbackType type = CallbackType.SCRIPT_RESULT;
    /**
     * 结果
     */
    private Boolean result = false;
    /**
     * 附加数据
     */
    private String attach;
    /**
     * 消息
     */
    private String message;
}