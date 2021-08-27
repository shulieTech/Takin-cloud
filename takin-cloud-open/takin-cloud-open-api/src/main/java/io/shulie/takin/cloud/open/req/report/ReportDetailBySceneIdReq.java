package io.shulie.takin.cloud.open.req.report;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @author 无涯
 * @Package io.shulie.takin.cloud.open.req.report
 * @date 2021/2/3 12:03 下午
 */
@Data
public class ReportDetailBySceneIdReq extends CloudUserCommonRequestExt {
    private Long sceneId;
}
