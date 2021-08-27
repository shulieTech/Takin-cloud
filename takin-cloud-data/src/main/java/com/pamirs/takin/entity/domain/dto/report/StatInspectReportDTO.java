package com.pamirs.takin.entity.domain.dto.report;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2021/4/15 3:32 下午
 * @Description:
 */
@Data
public class StatInspectReportDTO {
    /**
     * 请求总数
     */
    private Long totalRequest;

    /**
     * 平均成功率
     */
    private BigDecimal avgSuccessRate;

    /**
     * 平均RT
     */
    private BigDecimal avgRt;

    /**
     * 平均TPS
     */
    private BigDecimal avgTps;
}
