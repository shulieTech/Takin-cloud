package io.shulie.takin.cloud.open.req.report;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author 无涯
 * @Package io.shulie.takin.cloud.open.req.report
 * @date 2021/2/1 6:04 下午
 */
@Data
@ApiModel
public class UpdateReportConclusionReq extends CloudUserCommonRequestExt {
    private Long id;
    private String errorMessage;
    private Integer conclusion;
}
