package io.shulie.takin.cloud.biz.input.scenemanage;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class SceneTaskUpdateTpsInput extends CloudUserCommonRequestExt implements Serializable {
    private static final long serialVersionUID = -5189025661201526286L;

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
