package io.shulie.takin.cloud.biz.input.scenemanage;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class SceneStartTrialRunInput extends CloudUserCommonRequestExt {

    private Long sceneId;

    /**
     * 压测时长(秒)
     */
    private Long pressureTestSecond;
}
