package com.pamirs.takin.entity.domain.dto.strategy;

import lombok.Data;

/**
 * @Author 莫问
 * @Date 2020-05-15
 */
@Data
public class StrategyResultDTO {

    /**
     * 最小机器数量
     */
    private Integer min;

    /**
     * 最大
     */
    private Integer max;
}
