package io.shulie.takin.cloud.open.req.report;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2021/2/3 12:03 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportDetailBySceneIdReq extends CloudUserCommonRequestExt {
    private Long sceneId;
}
