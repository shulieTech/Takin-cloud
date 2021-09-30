package io.shulie.takin.cloud.web.entrypoint.request.scenemanage;


import io.shulie.takin.ext.content.trace.ContextExt;
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
public class SceneManageQueryRequest extends ContextExt {

    private Long sceneId;

    private String sceneName;

    private Integer status;

}
