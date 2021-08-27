package io.shulie.takin.cloud.biz.input.scenemanage;

import io.shulie.takin.cloud.common.bean.RuleBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mubai
 * @Date: 2020-10-29 14:05
 * @Description:
 */

@Data
public class SceneSlaRefBizInput implements Serializable {

    private static final long serialVersionUID = -3696634099481309635L;
    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "适用对象")
    private String[] businessActivity;

    @ApiModelProperty(value = "规则")
    private RuleBean rule;

    @ApiModelProperty(value = "状态")
    private Integer status = 0;
}
