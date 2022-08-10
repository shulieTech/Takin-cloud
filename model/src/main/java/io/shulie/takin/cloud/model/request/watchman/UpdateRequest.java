package io.shulie.takin.cloud.model.request.watchman;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 更新调度机
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@Schema(description = "更新调度机")
public class UpdateRequest {
    /**
     * 调度主键
     */
    @NotNull(message = "调度主键不能为空")
    @Min(value = 1, message = "调度主键不能小于0")
    @Schema(description = "调度主键", required = true)
    private Long watchmanId;
    /**
     * 公钥
     */
    @Schema(description = "公钥", required = true)
    private String publicKey;
}
