package io.shulie.takin.cloud.open.resp.report;

import java.io.Serializable;
import java.util.List;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @author moriarty
 */
@Data
public class ReportActivityResp extends CloudUserCommonRequestExt implements Serializable {

    private Long sceneId;

    private String sceneName;

    private Long reportId;

    private List<BusinessActivity> businessActivityList;

    @Data
    public static class BusinessActivity{
        String activityName;
        String bindRef;
    }
}

