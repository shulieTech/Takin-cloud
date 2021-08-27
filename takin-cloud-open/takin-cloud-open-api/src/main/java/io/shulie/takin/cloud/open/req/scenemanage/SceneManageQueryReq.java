package io.shulie.takin.cloud.open.req.scenemanage;

import java.io.Serializable;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @author 无涯
 * @Package io.shulie.takin.cloud.open.bean.scenemanage
 * @description:
 * @date 2020/10/22 8:07 下午
 */
@Data
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
