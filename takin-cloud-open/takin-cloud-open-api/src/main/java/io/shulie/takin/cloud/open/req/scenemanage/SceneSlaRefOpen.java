package io.shulie.takin.cloud.open.req.scenemanage;

import io.shulie.takin.cloud.common.bean.RuleBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author mubai
 * @date 2020-10-29 12:00
 */

@Data
public class SceneSlaRefOpen implements Serializable {

    private static final long serialVersionUID = -1911877711253414893L;

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "适用对象")
    private String[] businessActivity;

    @ApiModelProperty(value = "规则")
    private RuleBean rule;

    @ApiModelProperty(value = "状态")
    private Integer status = 0;

}
