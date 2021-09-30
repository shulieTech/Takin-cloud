package io.shulie.takin.cloud.open.req.scenemanage;

import io.shulie.takin.ext.content.trace.ContextExt;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author zhaoyong
 * 试跑启动参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneStartTrialRunRequestCloud extends ContextExt {

    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 压测时长(秒)
     */
    private Long pressureTestSecond;
}
