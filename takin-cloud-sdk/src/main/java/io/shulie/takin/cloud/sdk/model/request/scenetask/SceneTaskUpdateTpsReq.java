package io.shulie.takin.cloud.sdk.model.request.scenetask;

import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhaoyong
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SceneTaskUpdateTpsReq extends ContextExt {

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
