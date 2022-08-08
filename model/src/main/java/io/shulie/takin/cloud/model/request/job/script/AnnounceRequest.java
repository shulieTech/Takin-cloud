package io.shulie.takin.cloud.model.request.job.script;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 脚本校验任务下发参数
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@Schema(description = "脚本校验任务下发参数")
public class AnnounceRequest {
    /**
     * 回调路径
     */
    @Schema(description = "回调路径")
    private String callbackUrl;
    /**
     * 脚本路径
     */
    @Schema(description = "脚本路径")
    private String scriptPath;
    /**
     * 数据文件路径
     */
    @Schema(description = "数据文件路径")
    private List<String> dataFilePath;
    /**
     * 附件路径
     */
    @Schema(description = "附件路径")
    private List<String> attachmentsPath;
    /**
     * 插件路径
     */
    @Schema(description = "插件路径")
    private List<String> pluginPath;
}
