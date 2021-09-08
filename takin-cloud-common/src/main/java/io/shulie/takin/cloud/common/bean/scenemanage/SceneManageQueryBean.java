package io.shulie.takin.cloud.common.bean.scenemanage;

import java.io.Serializable;
import java.util.List;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 场景列表查询
 *
 * @author qianshui
 * @date 2020/4/17 下午2:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneManageQueryBean extends CloudUserCommonRequestExt implements Serializable {

    private Long sceneId;

    private String sceneName;

    private Integer status;

    /**
     * 压测场景类型：0普通场景，1流量调试
     */
    private Integer type;

    /**
     * 场景ids
     */
    private List<Long> sceneIds;

    private String lastPtStartTime;

    private String lastPtEndTime;
}
