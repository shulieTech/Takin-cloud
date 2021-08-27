package io.shulie.takin.cloud.open.req.report;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @ClassName WarnCreateReq
 * @Description 创建告警
 * @Author qianshui
 * @Date 2020/11/18 上午11:41
 */
@Data
public class WarnCreateReq extends CloudUserCommonRequestExt {
    private static final long serialVersionUID = 4235614311515898729L;

    private Long id;

    private Long ptId;

    private Long slaId;

    private String slaName;

    private Long businessActivityId;

    private String businessActivityName;

    private String warnContent;

    private Double realValue;

    private String warnTime;
}
