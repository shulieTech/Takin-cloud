package io.shulie.takin.cloud.biz.input.scenemanage;

import java.util.List;
import java.util.Map;

import io.shulie.takin.cloud.ext.content.enums.AssetTypeEnum;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/11/4 下午4:49
 */
@Data
public class SceneTaskStartInput {

    private Long sceneId;

    private List<EnginePluginInput> enginePlugins;

    private SceneInspectInput sceneInspectInput;

    private SceneTryRunInput sceneTryRunInput;

    private Boolean continueRead;

    /**
     * 流量类型
     *
     * @see AssetTypeEnum
     */
    private Integer assetType;

    private Long resourceId;

    private String resourceName;

    private Long operateId;

    private String operateName;

    /**
     * 占位符键值对
     */
    private Map<String, String> placeholderMap;
}
