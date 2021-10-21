package io.shulie.takin.cloud.biz.input.scenemanage;

import lombok.Data;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author qianshui
 * @date 2020/4/21 下午5:01
 */
@Data
public class SceneManageIdInput {

    @NotNull
    @ApiModelProperty(value = "ID")
    private Long id;
}
