package com.pamirs.takin.entity.domain.dto.report;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @Author 莫问
 * @Date 2020-04-21
 */
@Data
public class QueryData {

    /**
     *
     */
    private String time;

    /**
     * RT
     */
    private BigDecimal avgRt;

    /**
     * SA
     */
    private BigDecimal sa;

    /**
     * 成功率
     */
    private BigDecimal successRate;

    /**
     * TPS
     */
    private BigDecimal tps;
}
