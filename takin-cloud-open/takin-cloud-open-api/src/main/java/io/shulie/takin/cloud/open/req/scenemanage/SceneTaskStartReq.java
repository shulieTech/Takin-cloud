package io.shulie.takin.cloud.open.req.scenemanage;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @ClassName SceneTaskStartRequest
 * @Description
 * @Author qianshui
 * @Date 2020/11/4 下午4:46
 */
@Data
public class SceneTaskStartReq extends CloudUserCommonRequestExt {
    private static final long serialVersionUID = -508486534071711694L;

    private Long sceneId;
}