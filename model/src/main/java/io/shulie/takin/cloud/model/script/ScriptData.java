package io.shulie.takin.cloud.model.script;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * ClassName:    ScriptData
 * Package:    io.shulie.takin.cloud.model.script
 * Description:
 * Datetime:    2022/5/24   15:40
 * Author:   chenhongqiao@shulie.com
 */
@Data
@Schema(description = "脚本数据源")
public class ScriptData {
    @Schema(description = "数据源名称")
    private String name;
    @Schema(description = "数据源文件路径")
    @NotBlank(message = "数据文件路径不能为空")
    private String path;
    @Schema(description = "数据源变量格式")
    @NotBlank(message = "数据源变量格式不能为空")
    private String format;
}
