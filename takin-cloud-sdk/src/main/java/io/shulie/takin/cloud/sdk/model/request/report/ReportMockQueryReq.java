package io.shulie.takin.cloud.sdk.model.request.report;

import io.shulie.takin.cloud.ext.content.trace.PagingContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 莫问
 * @date 2020-04-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportMockQueryReq extends PagingContextExt {

    @ApiModelProperty(value = "报告ID")
    private Long reportId;

}
