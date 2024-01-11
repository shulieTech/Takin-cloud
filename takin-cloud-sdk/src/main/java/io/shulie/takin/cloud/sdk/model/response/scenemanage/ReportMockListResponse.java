package io.shulie.takin.cloud.sdk.model.response.scenemanage;

import lombok.Data;

/**
 * @author 莫问
 * @date 2020-04-18
 */
@Data
public class ReportMockListResponse {

    private Long reportId;
    private String startTime;
    private String endTime;
    private String appName;
    private String mockName;
    private String mockType;
    private String mockScript;
    private String mockStatus;
    private Long failureCount;
    private Long successCount;
    private Double avgRt;

}
