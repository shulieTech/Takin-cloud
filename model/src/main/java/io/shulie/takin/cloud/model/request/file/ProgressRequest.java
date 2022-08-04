package io.shulie.takin.cloud.model.request.file;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 文件资源管理进度更新
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Schema(description = "文件资源管理进度更新的请求")
public class ProgressRequest {
    /**
     * 标识
     */
    @Schema(description = "标识")
    Long id;
    /**
     * 总大小
     */
    @Schema(description = "总大小")
    Long totalSize;
    /**
     * 完成的大小
     */
    @Schema(description = "完成的大小")
    Long completeSize;
    /**
     * 是否完成
     */
    @Schema(description = "是否完成")
    Boolean completed;
}
