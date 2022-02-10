package io.shulie.takin.cloud.sdk.model.response.pressure;

import io.shulie.takin.cloud.ext.content.AbstractEntry;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Author: liyuanba
 * @Date: 2022/2/10 3:34 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PressureTaskResp extends AbstractEntry {
    @ApiModelProperty(value = "压测任务ID")
    private Long taskId;

    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(value = "场景类型:0-2常规模式,3流量调试,4巡检模式,5试跑模式")
    private Integer sceneType;

    @ApiModelProperty(value = "状态：0压测引擎启动中，1压测中，2压测停止，3失败")
    private Integer status;

    @ApiModelProperty(value = "压测启动时间")
    private Date gmtStart;

    @ApiModelProperty(value = "压测结束时间")
    private Date gmtEnd;

    @ApiModelProperty(value = "最后存活时间，健康监控")
    private Date gmtLive;

    @ApiModelProperty(value = "消息")
    private String message;
}
