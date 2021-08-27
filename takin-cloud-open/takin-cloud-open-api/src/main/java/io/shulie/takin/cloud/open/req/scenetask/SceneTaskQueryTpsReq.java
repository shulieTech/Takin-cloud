package io.shulie.takin.cloud.open.req.scenetask;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author zhaoyong
 */
@Data
public class SceneTaskQueryTpsReq extends CloudUserCommonRequestExt implements Serializable {
    private static final long serialVersionUID = 873912961392988591L;

    @NotNull
    @ApiModelProperty(value = "sceneId")
    private Long sceneId;

    @NotNull
    @ApiModelProperty(value = "reportId")
    private Long reportId;
}
