package io.shulie.takin.cloud.sdk.model.request.report;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;

/**
 * 创建告警
 *
 * @author qianshui
 * @date 2020/11/18 上午11:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarnCreateReq extends ContextExt {

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
