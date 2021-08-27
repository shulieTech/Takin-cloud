package io.shulie.takin.cloud.data.result.scenemanage;

import java.io.Serializable;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * 更新对应脚本时, 场景列表
 * 放弃使用
 * @author liuchuan
 */
@Deprecated
@Data
public class SceneManageListFromUpdateScriptResult extends CloudUserCommonRequestExt implements Serializable {

    private static final long serialVersionUID = -3967473117069389164L;

    /**
     * 场景id
     */
    private Long id;

    /**
     * 扩展字段
     */
    private String features;
}
