package io.shulie.takin.cloud.open.req.scenemanage;

import io.shulie.takin.cloud.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author moriarty
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptAssetBalanceReq extends CloudUserCommonRequestExt implements Serializable {

    private Long scriptDebugId;

    private Long cloudReportId;
}