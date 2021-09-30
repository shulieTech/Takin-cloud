package io.shulie.takin.cloud.open.req.scenemanage;

import io.shulie.takin.ext.content.trace.ContextExt;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
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
