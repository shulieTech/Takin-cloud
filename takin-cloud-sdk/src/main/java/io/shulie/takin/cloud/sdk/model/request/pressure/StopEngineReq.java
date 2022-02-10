package io.shulie.takin.cloud.sdk.model.request.pressure;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @Author: liyuanba
 * @Date: 2021/12/24 2:25 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StopEngineReq extends CheckEngineReq {
    @ApiModelProperty(value = "任务ID")
    private Long taskId;

    @ApiModelProperty(value = "场景ID")
    private Long id;

    @ApiModelProperty(value = "压测场景类型")
    private Integer sceneType;

    @ApiModelProperty(value = "删除容器延时执行时间（留时间给压测引擎自动停止），单位：毫秒,默认值0表示立即停止，可能导致数据丢失")
    private Long deleteJobDelay;
}
