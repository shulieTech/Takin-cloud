package io.shulie.takin.cloud.sdk.req.scenemanage;


import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2020/10/22 8:07 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneManageQueryReq extends ContextExt {

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
