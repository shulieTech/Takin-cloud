package io.shulie.takin.ext.content.enginecall;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author qianshui
 * @date 2020/5/9 下午2:06
 */
@Data
@ApiModel(description = "分配策略列表")
public class StrategyConfigExt implements Serializable {

    private static final long serialVersionUID = -8740307347149572470L;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "配置名称")
    private String strategyName;

    @ApiModelProperty(value = "并发数")
    private Integer threadNum;

    @ApiModelProperty(value = "tps数")
    private Integer tpsNum;

    @ApiModelProperty(value = "cpu")
    private BigDecimal cpuNum;

    @ApiModelProperty(value = "内存")
    private BigDecimal memorySize;

    //add by lipeng 20210623 添加限制cpu和限制内存
    @ApiModelProperty(value = "限制cpu")
    private BigDecimal limitCpuNum;

    @ApiModelProperty(value = "限制内存")
    private BigDecimal limitMemorySize;
    //add end

    @ApiModelProperty(value = "最后修改时间")
    private String updateTime;

    @ApiModelProperty(value = "发布方式")
    private String deploymentMethod;
}
