package io.shulie.takin.cloud.sdk.model.response.report;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author moriarty
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportActivityResp extends ContextExt {

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
