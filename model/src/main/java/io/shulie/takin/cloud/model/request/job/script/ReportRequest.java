package io.shulie.takin.cloud.model.request.job.script;

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 报告结果的响应
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@Schema(description = "脚本校验任务下发参数")
public class ReportRequest {
    /**
     * 脚本校验任务主键
     */
    @Schema(description = "任务主键")
    private Long id;
    /**
     * 脚本校验任务主键
     */
    @Schema(description = "任务主键")
    private Boolean completed;
    /**
     * 执行结果
     */
    @Schema(description = "执行结果")
    private String message;
}
