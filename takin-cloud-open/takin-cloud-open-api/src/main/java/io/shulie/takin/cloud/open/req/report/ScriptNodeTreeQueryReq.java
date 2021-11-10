package io.shulie.takin.cloud.open.req.report;

import java.io.Serializable;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author moriarty
 */
@Data
@ApiModel
public class ScriptNodeTreeQueryReq extends CloudUserCommonRequestExt {

    @ApiModelProperty(name = "sceneId",value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(name = "reportId",value = "报告ID")
    private Long reportId;
}
