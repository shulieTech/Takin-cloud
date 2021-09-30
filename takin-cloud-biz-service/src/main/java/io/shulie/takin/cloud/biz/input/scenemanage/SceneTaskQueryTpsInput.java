package io.shulie.takin.cloud.biz.input.scenemanage;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.swagger.annotations.ApiModelProperty;
import io.shulie.takin.ext.content.trace.ContextExt;

/**
 * @author zhaoyong
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTaskQueryTpsInput extends ContextExt {

    @NotNull
    @ApiModelProperty(value = "sceneId")
    private Long sceneId;

    @NotNull
    @ApiModelProperty(value = "reportId")
    private Long reportId;

}
