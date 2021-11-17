package io.shulie.takin.cloud.common.bean.sla;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author 莫问
 * @date 2020-04-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarnQueryParam extends PagingDevice {

    @ApiModelProperty(value = "报告ID")
    private Long reportId;

    @ApiModelProperty(value = "SLA ID")
    private Long slaId;

    @ApiModelProperty(value = "业务活动ID")
    private Long businessActivityId;

    @ApiModelProperty(value = "节点的xpathMD5值")
    private String bindRef;
}
