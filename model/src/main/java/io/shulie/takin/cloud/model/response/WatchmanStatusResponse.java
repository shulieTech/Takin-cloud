package io.shulie.takin.cloud.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 监听器状态
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "调度状态响应")
public class WatchmanStatusResponse {
    /**
     * 时间
     * <p>用于时效校验</p>
     */
    @Schema(description = "时间(用于时效校验)")
    long time;
    /**
     * 调度器的错误信息
     */
    @Schema(description = "错误信息")
    String message;
}
