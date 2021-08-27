package io.shulie.takin.cloud.web.entrypoint.response.strategy;

import lombok.Data;

/**
 * @Author 莫问
 * @Date 2020-05-15
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
