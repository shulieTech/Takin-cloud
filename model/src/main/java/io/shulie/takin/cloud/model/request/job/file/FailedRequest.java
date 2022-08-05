package io.shulie.takin.cloud.model.request.job.file;

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 失败时上报的请求
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@Schema(description = "失败时上报的请求")
public class FailedRequest {
    /**
     * 主键
     */
    @Schema(description = "主键")
    private Long id;
    /**
     * 消息
     */
    @Schema(description = "消息")
    private String message;
}
