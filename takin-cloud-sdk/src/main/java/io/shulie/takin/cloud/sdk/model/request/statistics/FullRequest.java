package io.shulie.takin.cloud.sdk.model.request.statistics;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;

/**
 * 全量统计响应
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FullRequest extends ContextExt {
    /**
     * 启动时间
     */
    @NotNull(message = "开始时间不能为空")
    @Positive(message = "开始时间不能是负数")
    private Long startTime;
    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @Positive(message = "结束时间不能是负数")
    private Long endTime;
    /**
     * 榜单数量
     */
    private Integer topNumber = 5;
}
