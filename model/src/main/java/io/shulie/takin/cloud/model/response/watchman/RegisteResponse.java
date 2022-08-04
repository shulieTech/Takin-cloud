package io.shulie.takin.cloud.model.response.watchman;

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 注册结果
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@Schema(description = "调度机注册结果")
public class RegisteResponse {
    /**
     * 调度机标识
     */
    @Schema(description = "调度机标识")
    Long id;
    /**
     * 签名
     */
    @Schema(description = "签名")
    String sign;
}