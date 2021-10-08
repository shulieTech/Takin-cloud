package io.shulie.takin.cloud.data.result.scenemanage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModelProperty;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneBusinessActivityRefBean;

/**
 * @author qianshui
 * @date 2020/4/17 下午9:47
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneBusinessActivityRefResult extends SceneBusinessActivityRefBean {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "绑定关系")
    private String bindRef;

    @ApiModelProperty(value = "应用IDS")
    private String applicationIds;

}
