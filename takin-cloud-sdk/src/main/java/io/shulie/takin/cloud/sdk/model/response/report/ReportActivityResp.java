package io.shulie.takin.cloud.sdk.model.response.report;

import java.io.Serializable;
import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;

/**
 * @author moriarty
 */
@Data
public class ReportActivityResp extends ContextExt implements Serializable {

    private Long sceneId;

    private Long reportId;

    private List<BusinessActivity> businessActivityList;

    @Data
    public static class BusinessActivity{
        String activityName;
        String bindRef;
    }
}

