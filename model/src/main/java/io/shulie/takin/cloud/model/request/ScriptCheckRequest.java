package io.shulie.takin.cloud.model.request;

import io.shulie.takin.cloud.model.script.ScriptData;
import io.shulie.takin.cloud.model.script.ScriptHeader;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * ClassName:    BuildPressureScriptRequest
 * Package:    io.shulie.takin.cloud.model.request
 * Description:脚本校验入参
 * Datetime:    2022/5/24   15:27
 * Author:   chenhongqiao@shulie.com
 */
@Data
@Schema(description = "脚本校验入参")
public class ScriptCheckRequest {
    @Schema(description = "脚本路径")
    @NotBlank(message = "脚本路径不能为空")
    private String scriptPath;
    @Schema(description = "插件路径，多个路径逗号隔开")
    private String pluginPaths;
    @Schema(description = "csv文件路径，多个路径逗号隔开")
    private String csvPaths;
}
