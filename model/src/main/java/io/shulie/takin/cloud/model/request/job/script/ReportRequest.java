package io.shulie.takin.cloud.model.request.job.script;

import javax.validation.constraints.NotNull;

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
    @NotNull(message = "脚本校验任务主键不能为空")
    @Schema(description = "脚本校验任务主键", required = true)
    private Long id;
    /**
     * 执行结果
     */
    @NotNull(message = "执行结果不能为空")
    @Schema(description = "执行结果", required = true)
    private Boolean result;
    /**
     * 执行结果描述
     */
    @Schema(description = "执行结果描述")
    private String message;
}
