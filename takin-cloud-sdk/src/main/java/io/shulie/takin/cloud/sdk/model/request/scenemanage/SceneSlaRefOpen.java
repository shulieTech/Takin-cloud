package io.shulie.takin.cloud.sdk.model.request.scenemanage;

import io.shulie.takin.cloud.sdk.model.common.RuleBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-10-29 12:00
 */

@Data
public class SceneSlaRefOpen {

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "适用对象")
    private String[] businessActivity;

    @ApiModelProperty(value = "规则")
    private RuleBean rule;

    @ApiModelProperty(value = "状态")
    private Integer status = 0;

}
