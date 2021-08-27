package io.shulie.takin.cloud.open.req.engine;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 引擎插件状态入参
 *
 * @author lipeng
 * @date 2021-01-14 4:17 下午
 */
@Data
@ApiModel("引擎插件状态入参")
public class EnginePluginStatusWrapperReq extends CloudUserCommonRequestExt implements Serializable {

    @ApiModelProperty("插件ID")
    private Long pluginId;
}
