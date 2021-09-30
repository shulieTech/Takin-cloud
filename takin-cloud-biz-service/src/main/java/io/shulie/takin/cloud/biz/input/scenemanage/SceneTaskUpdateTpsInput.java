package io.shulie.takin.cloud.biz.input.scenemanage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import io.shulie.takin.ext.content.trace.ContextExt;

/**
 * @author zhaoyong
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTaskUpdateTpsInput extends ContextExt {

    @NotNull
    @ApiModelProperty(value = "sceneId")
    private Long sceneId;

    @NotNull
    @ApiModelProperty(value = "reportId")
    private Long reportId;

    @NotNull
    @ApiModelProperty(value = "tpsNum")
    private Long tpsNum;
}
