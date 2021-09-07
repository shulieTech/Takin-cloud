package com.pamirs.takin.entity.domain.query;

import java.io.Serializable;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author fanxx
 * @date 2020/5/15 下午9:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PressureMachineQueryParam extends PagingDevice implements Serializable {
    private static final long serialVersionUID = 4896574213234382011L;

    @ApiModelProperty(value = "平台ID")
    private Long platformId;

    @ApiModelProperty(value = "开通模式")
    private Integer openType;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
