package com.pamirs.takin.entity.domain.entity.schedule;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class ScheduleRecord {
    private Long id;

    private Long sceneId;

    private Long taskId;

    // todo 新增字段 客户id
    private Long customerId;

    private Integer podNum;

    private String podClass;

    private Integer status;

    private BigDecimal cpuCoreNum;

    private BigDecimal memorySize;

    private Date createTime;

}