package io.shulie.takin.cloud.sdk.model.response.strategy;

import lombok.Data;

/**
 * @author 莫问
 * @date 2020-05-15
 */
@Data
public class StrategyResponse {

    /**
     * 最小机器数量
     */
    private Integer min;

    /**
     * 最大
     */
    private Integer max;
}
