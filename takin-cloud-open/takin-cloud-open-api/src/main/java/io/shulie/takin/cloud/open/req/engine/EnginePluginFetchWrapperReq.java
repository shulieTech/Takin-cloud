package io.shulie.takin.cloud.open.req.engine;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 获取引擎插件列表入参
 *
 * @author lipeng
 * @date 2021-01-20 3:45 下午
 */
@Data
@ApiModel("获取引擎插件列表入参")
public class EnginePluginFetchWrapperReq extends CloudUserCommonRequestExt implements Serializable {

    private List<String> pluginTypes;

}