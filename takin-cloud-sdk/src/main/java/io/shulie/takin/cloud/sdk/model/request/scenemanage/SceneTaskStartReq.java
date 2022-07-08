package io.shulie.takin.cloud.sdk.model.request.scenemanage;

import java.util.List;
import java.util.Map;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.sdk.model.request.engine.EnginePluginsRefOpen;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/11/4 下午4:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTaskStartReq extends ContextExt {

    private Long sceneId;

    /**
     * cloud的统一接收参数
     */
    private String resourceName;

    /**
     * 使用下面enginePlugins，包含id和版本号
     */
    @Deprecated
    private List<Long> enginePluginIds;

    private List<SceneTaskStartRequest.EnginePluginInput> enginePlugins;

    private Boolean leakSqlEnable;

    private Boolean continueRead = false;

    /**
     * 占位符键值对
     */
    private Map<String, String> placeholderMap;

}