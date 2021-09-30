package io.shulie.takin.cloud.data.result.engine;

import lombok.Data;


/**
 * 引擎插件简介结果
 *
 * @author lipeng
 * @date 2021-01-20 5:00 下午
 */
@Data
public class EnginePluginSimpleInfoResult {

    private Long pluginId;

    private String pluginType;

    private String pluginName;

    private String gmtUpdate;

}
