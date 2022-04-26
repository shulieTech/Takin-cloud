package io.shulie.takin.cloud.sdk.model.response.report;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @author moriarty
 */
@Data
public class ReportActivityResp implements Serializable {

    private Long sceneId;

    private String sceneName;

    private Long reportId;

    private List<BusinessActivity> businessActivityList;

    @Data
    public static class BusinessActivity {
        String activityName;
        String bindRef;
    }
}
