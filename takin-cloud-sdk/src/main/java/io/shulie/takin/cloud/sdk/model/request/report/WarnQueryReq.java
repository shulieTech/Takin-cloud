package io.shulie.takin.cloud.sdk.model.request.report;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 莫问
 * @date 2020-04-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarnQueryReq extends PagingDevice {

    @ApiModelProperty(value = "报告ID")
    private Long reportId;

    @ApiModelProperty(value = "SLA ID")
    private Long slaId;

    @ApiModelProperty(value = "业务活动ID")
    private Long businessActivityId;
}
