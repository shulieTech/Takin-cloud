package com.pamirs.takin.entity.domain.dto.report;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 机器性能详情
 *
 * @author qianshui
 * @date 2020/7/22 下午2:59
 */
@ApiModel
@Data
public class MachineDetailDTO implements Serializable {

    private static final long serialVersionUID = 7813020434970519614L;

    @ApiModelProperty(value = "主机ip")
    private String machineIp;

    @ApiModelProperty(value = "cpu")
    private Integer cpuNum;

    @ApiModelProperty(value = "内存")
    private Integer memorySize;

    @ApiModelProperty(value = "磁盘")
    private Integer diskSize;

    @ApiModelProperty(value = "带宽")
    private BigDecimal mbps;

    @ApiModelProperty(value = "是否风险机器")
    private Boolean riskFlag;

    private MachineTPSTargetDTO tpsTarget;

    @ApiModel
    @Data
    public class MachineTPSTargetDTO implements Serializable {

        private static final long serialVersionUID = -7728660288117937429L;

        private String[] tps;

        private String[] cpu;

        private String[] load;

        private String[] memory;

        private String[] io;

        private String[] mbps;
    }
}
