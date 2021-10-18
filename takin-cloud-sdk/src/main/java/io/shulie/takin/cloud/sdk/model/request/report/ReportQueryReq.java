package io.shulie.takin.cloud.sdk.model.request.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author shiyajian
 * create: 2020-10-20
 */

@Data
@ApiModel
public class ReportQueryReq {

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
