package com.pamirs.takin.entity.domain.entity.report;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ReportMock implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long reportId;
    private Date startTime;
    private Date endTime;
    private String appName;
    private String mockName;
    private String mockType;
    private String mockScript;
    private Integer mockStatus;
    private Long failureCount;
    private Long successCount;
    private Double avgRt;
}
