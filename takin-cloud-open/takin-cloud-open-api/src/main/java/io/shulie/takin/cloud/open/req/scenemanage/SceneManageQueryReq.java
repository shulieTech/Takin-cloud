package io.shulie.takin.cloud.open.req.scenemanage;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;

/**
 * @author 无涯
 * @date 2020/10/22 8:07 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneManageQueryReq extends CloudUserCommonRequestExt implements Serializable {

    private Long sceneId;

    private String sceneName;

    private Integer status;

    /**
     * 场景ids (逗号进行分割)
     */
    private String sceneIds ;

    private String lastPtStartTime ;

    private String lastPtEndTime ;

}
