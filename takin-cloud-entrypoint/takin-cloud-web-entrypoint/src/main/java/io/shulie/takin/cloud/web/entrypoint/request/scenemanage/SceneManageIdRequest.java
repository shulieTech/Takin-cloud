package io.shulie.takin.cloud.web.entrypoint.request.scenemanage;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName SceneManageIdRequest
 * @Description
 * @Author qianshui
 * @Date 2020/4/21 下午5:01
 */
@Data
public class SceneManageIdRequest implements Serializable {

    private static final long serialVersionUID = 5258828941952507100L;

    @NotNull
    @ApiModelProperty(value = "ID")
    private Long id;
}
