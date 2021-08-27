package com.pamirs.takin.entity.domain.dto.report;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName RiskMacheineDTO
 * @Description 风险机器列表
 * @Author qianshui
 * @Date 2020/7/22 下午2:57
 */
@ApiModel
@Data
public class RiskMacheineDTO implements Serializable {

    private static final long serialVersionUID = -821315409231651171L;

    @ApiModelProperty(value = "机器")
    private String machineIp;

    @ApiModelProperty(value = "风险")
    private String riskContent;

}
