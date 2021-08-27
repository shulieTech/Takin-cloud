package io.shulie.takin.cloud.open.req.scenetask;

import java.io.Serializable;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xr.l
 */
@Data
public class SceneTryRunTaskCheckReq extends CloudUserCommonRequestExt implements Serializable {

    @ApiModelProperty(value = "场景Id")
    private Long sceneId;

    @ApiModelProperty(value = "报告Id")
    private Long reportId;

}
