package io.shulie.takin.cloud.web.entrypoint.request.scenemanage;

import java.io.Serializable;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @ClassName SceneManageQueryRequest
 * @Description 场景列表查询
 * @Author qianshui
 * @Date 2020/4/17 下午2:18
 */
@Data
public class SceneManageQueryRequest extends CloudUserCommonRequestExt implements Serializable {

    private Long sceneId;

    private String sceneName;

    private Integer status;

}
