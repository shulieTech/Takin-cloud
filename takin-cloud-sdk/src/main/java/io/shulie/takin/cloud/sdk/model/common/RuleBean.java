package io.shulie.takin.cloud.sdk.model.common;

import java.math.BigDecimal;

import lombok.Data;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author qianshui
 * @date 2020/4/18 上午10:58
 */
@Data
public class RuleBean {

    @ApiModelProperty(value = "指标类型")
    private Integer indexInfo;

    @ApiModelProperty(value = "条件")
    private Integer condition;

    @ApiModelProperty(value = "满足值")
    private BigDecimal during;

    @ApiModelProperty(value = "连续触发次数")
    private Integer times;

    public RuleBean() {}

    public RuleBean(Integer indexInfo, Integer condition, BigDecimal during, Integer times) {
        this.setIndexInfo(indexInfo);
        this.setCondition(condition);
        this.setDuring(during);
        this.setTimes(times);
    }
}
