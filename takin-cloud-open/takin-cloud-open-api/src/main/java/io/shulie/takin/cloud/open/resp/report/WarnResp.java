package io.shulie.takin.cloud.open.resp.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mubai
 * @Date: 2020-11-02 17:08
 * @Description:
 */

@Data
public class WarnResp implements Serializable {

    private static final long serialVersionUID = 7641125133561940724L;

    @ApiModelProperty(value = "报告 ID")
    private Long reportId;

    @ApiModelProperty(value = "SLA ID")
    private Long slaId;

    @ApiModelProperty(value = "SLA名称")
    private String slaName;

    @ApiModelProperty(value = "活动ID")
    private String businessActivityId;

    @ApiModelProperty(value = "活动名称")
    private String businessActivityName;

    @ApiModelProperty(value = "警告次数")
    private Long total;

    @ApiModelProperty(value = "规则明细")
    private String content;

    @ApiModelProperty(value = "最新警告时间")
    private String lastWarnTime;
}
