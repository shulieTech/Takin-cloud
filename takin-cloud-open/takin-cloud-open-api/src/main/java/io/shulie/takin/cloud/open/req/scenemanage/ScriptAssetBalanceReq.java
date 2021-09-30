package io.shulie.takin.cloud.open.req.scenemanage;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

import java.io.Serializable;

/**
 * @author moriarty
 */
@Data
public class ScriptAssetBalanceReq extends CloudUserCommonRequestExt implements Serializable {

    private Long scriptDebugId;

    private Long cloudReportId;
}