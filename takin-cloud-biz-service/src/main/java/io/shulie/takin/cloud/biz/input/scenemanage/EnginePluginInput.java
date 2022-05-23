package io.shulie.takin.cloud.biz.input.scenemanage;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author moriarty
 */
@Data
@Accessors(chain = true)
public class EnginePluginInput {

    private Long pluginId;

    private String pluginVersion;
}
