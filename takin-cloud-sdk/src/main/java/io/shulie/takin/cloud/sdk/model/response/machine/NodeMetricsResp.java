package io.shulie.takin.cloud.sdk.model.response.machine;

import lombok.Data;

import java.math.BigDecimal;

/**
 * ClassName:    NodeMetricsResp
 * Package:    io.shulie.takin.cloud.sdk.model.response.machine
 * Description:
 * Datetime:    2022/7/9   14:18
 * Author:   chenhongqiao@shulie.com
 */
@Data
public class NodeMetricsResp {
    private String name;
    private String status;
    private BigDecimal cpu;
    private BigDecimal memory;
    private String nodeIp;
}
