package io.shulie.takin.cloud.model.request.job.script;

import lombok.Data;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

import io.shulie.takin.cloud.model.script.ScriptData;
import io.shulie.takin.cloud.model.script.ScriptHeader;

/**
 * 单接口压测
 *
 * @author chenhongqiao@shulie.com
 */
@Data
@Schema(description = "构建脚本入参")
public class BuildRequest {
    @Schema(description = "接口名称")
    @NotBlank(message = "接口名称不能为空")
    private String name;
    @Schema(description = "请求地址")
    @NotBlank(message = "请求地址不能为空")
    private String url;
    @Schema(description = "请求类型")
    @NotBlank(message = "请求类型不能为空")
    private String method;
    @Schema(description = "请求头集合")
    private List<ScriptHeader> headers;
    @Schema(description = "请求体")
    private String body;
    @Schema(description = "数据源集合")
    @Valid
    private List<ScriptData> datas;

}
