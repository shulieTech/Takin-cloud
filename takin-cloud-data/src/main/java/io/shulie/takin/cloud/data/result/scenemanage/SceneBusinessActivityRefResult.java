package io.shulie.takin.cloud.data.result.scenemanage;

import io.shulie.takin.cloud.common.bean.scenemanage.SceneBusinessActivityRefBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName SceneBusinessActivityRefResult
 * @Description
 * @Author qianshui
 * @Date 2020/4/17 下午9:47
 */
@Data
public class SceneBusinessActivityRefResult extends SceneBusinessActivityRefBean {

    private static final long serialVersionUID = -6384484202725660595L;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "绑定关系")
    private String bindRef;

    @ApiModelProperty(value = "应用IDS")
    private String applicationIds;

}
