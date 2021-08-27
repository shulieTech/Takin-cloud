package io.shulie.takin.cloud.open.req.scenemanage;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhaoyong
 * 试跑启动参数
 */
@Data
public class SceneStartTrialRunRequestCloud extends CloudUserCommonRequestExt implements Serializable {
    private static final long serialVersionUID = 2712267844636591660L;

    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 压测时长(秒)
     */
    private Long pressureTestSecond;
}
