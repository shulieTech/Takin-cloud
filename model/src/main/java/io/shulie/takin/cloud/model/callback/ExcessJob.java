package io.shulie.takin.cloud.model.callback;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.shulie.takin.cloud.model.callback.basic.Base;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.constant.enums.ExcessJobType;

/**
 * 额外任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ExcessJob extends Base<Long> {
    /**
     * 回调类型
     */
    private CallbackType type = CallbackType.SCHEDULE;
    /**
     * 额外任务
     */
    private ExcessJobType jobType;
    /**
     * 额外任务
     */
    private long jobId;
    /**
     * 执行结果
     */
    private String content;
    /**
     * 是否完成
     */
    private Boolean completed;
}
