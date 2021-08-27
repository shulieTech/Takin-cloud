package io.shulie.takin.cloud.biz.input.scenemanage;

import java.io.Serializable;
import java.util.List;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @ClassName SceneManageQueryInput
 * @Description 场景列表查询
 * @Author qianshui
 * @Date 2020/4/17 下午2:18
 */
@Data
public class SceneManageQueryInput extends CloudUserCommonRequestExt implements Serializable {

    private Long sceneId;

    private String sceneName;

    private Integer status;

    /**
     * 场景ids
     */
    private List<Long> sceneIds ;

    private String lastPtStartTime ;

    private String lastPtEndTime ;
}
