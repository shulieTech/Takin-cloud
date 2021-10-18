package io.shulie.takin.cloud.sdk.model.request.scenetask;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


/**
 * @author zhaoyong
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTaskQueryTpsReq extends ContextExt {

    @NotNull
    @ApiModelProperty(value = "sceneId")
    private Long sceneId;

    @NotNull
    @ApiModelProperty(value = "reportId")
    private Long reportId;
}
