package io.shulie.takin.cloud.sdk.model.request.scenemanage;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
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