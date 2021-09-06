package io.shulie.takin.ext.content.enginecall;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author qianshui
 * @date 2020/5/9 下午2:06
 */
@Data
public class StrategyConfigExt implements Serializable {

    private static final long serialVersionUID = -8740307347149572470L;

    private Long id;

    private String strategyName;

    private Integer threadNum;

    private Integer tpsNum;

    private BigDecimal cpuNum;

    private BigDecimal memorySize;

    //add by lipeng 20210623 添加限制cpu和限制内存
    private BigDecimal limitCpuNum;

    private BigDecimal limitMemorySize;
    //add end

    private String updateTime;

    private String deploymentMethod;
}
