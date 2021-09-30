package io.shulie.takin.cloud.biz.input.scenemanage;

import lombok.Data;

import java.util.List;

import io.shulie.takin.ext.content.trace.ContextExt;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/11/4 下午4:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTaskStartInput extends ContextExt {

    private Long sceneId;

    private List<EnginePluginInput> enginePlugins;

    private SceneInspectInput sceneInspectInput;

    private SceneTryRunInput sceneTryRunInput;

    private Boolean continueRead;
}
