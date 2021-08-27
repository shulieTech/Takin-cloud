package io.shulie.takin.cloud.open.req.report;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @author 无涯
 * @Package io.shulie.takin.cloud.open.req.report
 * @date 2020/12/17 1:25 下午
 */
@Data
public class JtlDownloadReq extends CloudUserCommonRequestExt {
    private Long sceneId;
    private Long reportId;
}
