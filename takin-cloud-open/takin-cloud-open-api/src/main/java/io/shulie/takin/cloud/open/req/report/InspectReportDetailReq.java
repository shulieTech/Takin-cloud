package io.shulie.takin.cloud.open.req.report;

import java.io.Serializable;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2021/4/15 4:37 下午
 * @Description:
 */
@Data
public class InspectReportDetailReq extends CloudUserCommonRequestExt implements Serializable {
    private static final long serialVersionUID = 8440924128595401088L;

    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
