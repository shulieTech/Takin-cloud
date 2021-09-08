package io.shulie.takin.cloud.open.req.scenemanage;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author mubai
 * @date 2020-11-16 19:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneManageQueryByIdsReq extends CloudUserCommonRequestExt implements Serializable {
    private static final long serialVersionUID = -3706985653572707716L;

    private List<Long> sceneIds;
}
