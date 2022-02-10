package io.shulie.takin.cloud.sdk.model.request.pressure;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: liyuanba
 * @Date: 2022/2/10 2:47 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CheckEngineReq extends ContextExt {
    @ApiModelProperty(value = "任务ID")
    private Long taskId;

    @ApiModelProperty(value = "场景ID")
    private Long id;

    @ApiModelProperty(value = "压测场景类型")
    private Integer sceneType;
}
