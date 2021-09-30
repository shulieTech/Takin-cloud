package com.pamirs.takin.entity.domain.vo.report;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.shulie.takin.ext.content.trace.PagingContextExt;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
public class ReportQueryParam extends PagingContextExt {

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

    /**
     * 报告类型；0普通场景，1流量调试
     */
    private Integer type;
}
