package io.shulie.takin.cloud.biz.input.scenemanage;

import java.util.List;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/11/4 下午4:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTaskStartInput extends CloudUserCommonRequestExt {

    private Long sceneId;

    private List<EnginePluginInput> enginePlugins;

    private SceneInspectInput sceneInspectInput;

    private SceneTryRunInput sceneTryRunInput;

    private Boolean continueRead;

    /**
     * 流量类型
     * @see io.shulie.takin.ext.content.enums.AssetTypeEnum
     */
    private Integer assetType;

    private Long resourceId;

    private String resourceName;

    /**
     * 创建者
     */
    private Long creatorId;
}
