package io.shulie.takin.cloud.sdk.req.scenemanage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;

/**
 * @author moriarty
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptAssetBalanceReq extends ContextExt {
    /**
     * 脚本调试主键
     */
    private Long scriptDebugId;
    /**
     * 报告主键
     */
    private Long cloudReportId;
}