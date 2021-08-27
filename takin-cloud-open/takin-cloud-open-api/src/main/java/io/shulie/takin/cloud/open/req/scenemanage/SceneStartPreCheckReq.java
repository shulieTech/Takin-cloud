package io.shulie.takin.cloud.open.req.scenemanage;

import java.io.Serializable;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @author moriarty
 */
@Data
public class SceneStartPreCheckReq extends CloudUserCommonRequestExt implements Serializable {

    private Long sceneId;

}