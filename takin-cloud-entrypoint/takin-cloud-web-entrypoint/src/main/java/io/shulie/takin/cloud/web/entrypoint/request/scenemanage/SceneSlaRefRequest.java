package io.shulie.takin.cloud.web.entrypoint.request.scenemanage;

import io.shulie.takin.cloud.common.bean.RuleBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author shulie
 */
@Data
public class SceneSlaRefRequest implements Serializable {
    private static final long serialVersionUID = 7339894846325449990L;

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "适用对象")
    private String[] businessActivity;

    @ApiModelProperty(value = "规则")
    private RuleBean rule;

    @ApiModelProperty(value = "状态")
    private Integer status = 0;
}
