package io.shulie.takin.cloud.open.req.scenemanage;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: mubai
 * @Date: 2020-11-16 19:33
 * @Description:
 */

@Data
public class SceneManageQueryByIdsReq  extends CloudUserCommonRequestExt implements Serializable {
    private static final long serialVersionUID = -3706985653572707716L;

    private List<Long> sceneIds;
}
