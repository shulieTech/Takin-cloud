package io.shulie.takin.cloud.model.script;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * ClassName:    ScriptHeader
 * Package:    io.shulie.takin.cloud.model.script
 * Description:
 * Datetime:    2022/5/24   15:39
 * Author:   chenhongqiao@shulie.com
 */
@Data
@Schema(description = "脚本请求头")
public class ScriptHeader {
    @Schema(description = "请求头key")
    private String key;
    @Schema(description = "请求头Value")
    private String value;
}
