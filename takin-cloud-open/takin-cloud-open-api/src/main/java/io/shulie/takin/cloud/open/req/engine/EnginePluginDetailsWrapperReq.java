package io.shulie.takin.cloud.open.req.engine;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 获取引擎插件详情入参
 *
 * @author lipeng
 * @date 2021-01-20 3:49 下午
 */
@Data
@ApiModel("获取引擎插件详情入参")
public class EnginePluginDetailsWrapperReq extends CloudUserCommonRequestExt implements Serializable {

    private Long pluginId;
}
