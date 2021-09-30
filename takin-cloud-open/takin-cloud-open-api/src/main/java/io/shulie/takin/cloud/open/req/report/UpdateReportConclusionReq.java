package io.shulie.takin.cloud.open.req.report;

import io.shulie.takin.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;

/**
 * @author 无涯
 * @date 2021/2/1 6:04 下午
 */
@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
public class UpdateReportConclusionReq extends ContextExt {
    private Long id;
    private String errorMessage;
    private Integer conclusion;
}
