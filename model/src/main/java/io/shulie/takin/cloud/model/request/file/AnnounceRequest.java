package io.shulie.takin.cloud.model.request.file;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 文件资源管理下发
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@Schema(description = "文件资源管理下发的请求")
public class AnnounceRequest {
    @Schema(description = "文件列表")
    List<File> fileList;
    @Schema(description = "调度器主键列表")
    List<Long> watchmanIdList;

    @Data
    public static class File {

        /**
         * 文件路径
         */
        @Schema(description = "文件路径")
        private String path;
        /**
         * 文件摘要
         */
        @Schema(description = "文件摘要")
        private String sign;
        /**
         * 下载地址
         */
        @Schema(description = "下载地址")
        private String downloadUrl;
    }
}
