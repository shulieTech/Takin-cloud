package io.shulie.takin.cloud.open.req.scenemanage;


import io.shulie.takin.ext.content.trace.ContextExt;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author moriarty
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SceneStartPreCheckReq extends ContextExt {

    private Long sceneId;

}