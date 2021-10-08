package io.shulie.takin.cloud.biz.input.scenemanage;

import io.shulie.takin.cloud.ext.content.enums.AssetTypeEnum;
import lombok.Data;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
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

    /**
     * 流量类型
     * @see AssetTypeEnum
     */
    private Integer assetType;

    private Long resourceId;

    private String resourceName;

    /**
     * 创建者
     */
    private Long creatorId;
}
