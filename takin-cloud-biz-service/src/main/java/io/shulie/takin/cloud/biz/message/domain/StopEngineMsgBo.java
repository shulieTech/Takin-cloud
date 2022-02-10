package io.shulie.takin.cloud.biz.message.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: liyuanba
 * @Date: 2022/2/10 1:54 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StopEngineMsgBo extends AbstractMessageContentBo {
    private Long sceneId;
    private Long taskId;
    private Long tenantId;
    private Integer sceneType;
    private String jobName;

    @Override
    public String getKey() {
        return String.valueOf(taskId);
    }
}
