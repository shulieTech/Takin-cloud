package io.shulie.takin.cloud.model.callback;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.model.callback.basic.Base;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.model.callback.basic.JobExample;

/**
 * 定时任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Schedule extends Base<JobExample> {
    /**
     * 回调类型
     */
    private CallbackType type = CallbackType.SCHEDULE;
    /**
     * 定时任务类型
     */
    private Integer scheduleType;
    /**
     * 是否完成
     */
    private Boolean completed;
    /**
     * 执行结果
     */
    private String content;
}
