package io.shulie.takin.cloud.web.entrypoint.request.scenemanage;

import java.util.List;

import io.shulie.takin.cloud.biz.input.scenemanage.EnginePluginInput;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/11/4 下午4:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTaskStartRequest extends ContextExt {

    private Long sceneId;

    private List<Long> enginePluginIds;

    private List<EnginePluginInput> enginePlugins;

    private Boolean continueRead;
}
