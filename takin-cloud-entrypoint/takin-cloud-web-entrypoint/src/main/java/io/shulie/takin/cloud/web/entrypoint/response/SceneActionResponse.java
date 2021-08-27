package io.shulie.takin.cloud.web.entrypoint.response;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName SceneActionResponse
 * @Description
 * @Author qianshui
 * @Date 2020/11/13 上午11:03
 */
@Data
@ApiModel("状态检查返回值")
public class SceneActionResponse implements Serializable {

    private static final long serialVersionUID = -2592300523249555242L;

    @ApiModelProperty(value = "状态值")
    private Long data;

    @ApiModelProperty(value = "错误信息")
    private List<String> msg;
}