package io.shulie.takin.cloud.sdk.model.request.report;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.shulie.takin.cloud.ext.content.trace.PagingContextExt;

/**
 * @author shiyajian
 * create: 2020-10-20
 */

@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
public class ReportQueryReq extends PagingContextExt {

    /**
     * 场景名称
     */
    @ApiModelProperty(name = "sceneName", value = "场景名称")
    private String sceneName;

    /**
     * 压测开始时间
     */
    @ApiModelProperty(value = "压测开始时间")
    private String startTime;

    /**
     * 压测结束时间
     */
    @ApiModelProperty(value = "压测结束时间")
    private String endTime;

}
