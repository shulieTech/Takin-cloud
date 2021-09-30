package io.shulie.takin.cloud.open.req.scenemanage;


import javax.validation.constraints.NotNull;

import io.shulie.takin.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author 无涯
 * @date 2020/10/22 8:06 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneManageDeleteReq extends ContextExt {

    @NotNull
    @ApiModelProperty(value = "ID")
    private Long id;
}
