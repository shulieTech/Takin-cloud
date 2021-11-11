package io.shulie.takin.cloud.open.req.report;


import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author moriarty
 */
@Data
public class ReportTrendQueryReq extends CloudUserCommonRequestExt {


    /**
     * 报表ID
     */
    @ApiModelProperty(value = "报表ID")
    private Long reportId;

    /**
     * 场景ID
     */
    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    /**
     * xpathMD5
     */
    @ApiModelProperty(value = "节点的xpathMD5值，即bindRef,老版本的压测场景是业务活动名称，新版本是md5值")
    private String xpathMd5;

}
