package io.shulie.takin.cloud.sdk.model.request.scenemanage;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author mubai
 * @date 2020-11-16 19:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneManageQueryByIdsReq extends ContextExt {

    private List<Long> sceneIds;
}
