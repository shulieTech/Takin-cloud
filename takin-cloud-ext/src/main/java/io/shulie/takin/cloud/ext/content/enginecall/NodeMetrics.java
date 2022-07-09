package io.shulie.takin.cloud.ext.content.enginecall;

import lombok.Data;

import java.math.BigDecimal;

/**
 * ClassName:    NodeList
 * Package:    io.shulie.takin.cloud.ext.content.enginecall
 * Description:
 * Datetime:    2022/7/8   10:47
 * Author:   chenhongqiao@shulie.com
 */
@Data
public class NodeMetrics {
    private String name;
    private String status;
    private BigDecimal cpu;
    private BigDecimal memory;
    private String nodeIp;
}
