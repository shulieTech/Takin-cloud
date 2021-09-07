package com.pamirs.takin.entity.domain.dto.report;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author 莫问
 * @date 2020-04-20
 */
@ApiModel
@Data
public class BusinessActivityDTO {

    @ApiModelProperty(value = "活动ID")
    private Long businessActivityId;

    @ApiModelProperty(value = "活动名称")
    private String businessActivityName;
}
