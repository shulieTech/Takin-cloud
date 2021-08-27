package io.shulie.takin.cloud.open.req.scenetask;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2021/4/14 4:36 下午
 * @Description:
 */
@Data
public class TaskInspectStopReq extends CloudUserCommonRequestExt implements Serializable {
    private static final long serialVersionUID = -9162208161836587615L;

    @ApiModelProperty(value = "场景Id")
    @NotNull
    private Long sceneId;
}
