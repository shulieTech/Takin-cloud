package io.shulie.takin.cloud.open.req.scenetask;


import io.shulie.takin.ext.content.trace.ContextExt;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xr.l
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTryRunTaskCheckReq extends ContextExt {

    @ApiModelProperty(value = "场景Id")
    private Long sceneId;

    @ApiModelProperty(value = "报告Id")
    private Long reportId;

}
