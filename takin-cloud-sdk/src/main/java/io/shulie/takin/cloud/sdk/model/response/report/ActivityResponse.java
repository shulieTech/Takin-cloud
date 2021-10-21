package io.shulie.takin.cloud.sdk.model.response.report;

import lombok.Data;

import io.swagger.annotations.ApiModelProperty;

/**
 * TODO
 *
 * @author 张天赐
 */
@Data
public class ActivityResponse {
    @ApiModelProperty(value = "活动ID")
    private Long businessActivityId;

    @ApiModelProperty(value = "活动名称")
    private String businessActivityName;
}
