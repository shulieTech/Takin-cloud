package io.shulie.takin.cloud.open.req.scenemanage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.ext.content.user.CloudUserExt;

/**
 * @author moriarty
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptAssetBalanceReq extends CloudUserExt {
    /**
     * 脚本调试主键
     */
    private Long scriptDebugId;
    /**
     * 报告主键
     */
    private Long cloudReportId;
}