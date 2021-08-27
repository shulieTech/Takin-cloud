package io.shulie.takin.cloud.biz.input.scenemanage;

import io.shulie.takin.cloud.common.bean.RuleBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: mubai
 * @Date: 2020-10-29 11:56
 * @Description:
 */

@Data
public class SceneSlaRefInput {
    private static final long serialVersionUID = 4747478435828708203L;

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "适用对象")
    private String[] businessActivity;

    @ApiModelProperty(value = "规则")
    private RuleBean rule;

    @ApiModelProperty(value = "状态")
    private Integer status = 0;
}
