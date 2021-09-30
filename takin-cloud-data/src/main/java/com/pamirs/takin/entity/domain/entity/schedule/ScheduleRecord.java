package com.pamirs.takin.entity.domain.entity.schedule;

import java.util.Date;
import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.ext.content.trace.ContextExt;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleRecord extends ContextExt {
    private Long id;

    private Long sceneId;

    private Long taskId;

    private Integer podNum;

    private String podClass;

    private Integer status;

    private BigDecimal cpuCoreNum;

    private BigDecimal memorySize;

    private Date createTime;

}