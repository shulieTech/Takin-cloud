package io.shulie.takin.cloud.open.req.scenemanage;

import io.shulie.takin.cloud.common.pojo.AbstractEntry;
import io.shulie.takin.cloud.web.entrypoint.validation.groups.Add;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.groups.Default;

/**
 * @Author: liyuanba
 * @Date: 2021/10/26 1:51 下午
 */
@Data
public class ScriptAnalyzeRequest extends AbstractEntry {
    @ApiModelProperty(name = "scriptFile", value = "脚本文件")
    @NotBlank(message = "脚本文件不能为空", groups = {Add.class, Default.class})
    private String scriptFile;
}
