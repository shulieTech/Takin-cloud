package io.shulie.takin.cloud.open.req.scenetask;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class SceneTaskUpdateTpsReq extends CloudUserCommonRequestExt implements Serializable {
    private static final long serialVersionUID = -948341850155816814L;

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
