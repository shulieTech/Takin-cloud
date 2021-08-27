package io.shulie.takin.cloud.data.result.scenemanage;

import java.io.Serializable;

import io.shulie.takin.cloud.common.bean.RuleBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName SceneSlaRefResult
 * @Description
 * @Author qianshui
 * @Date 2020/4/18 上午10:59
 */
@Data
public class SceneSlaRefResult implements Serializable {

    private static final long serialVersionUID = 5117439939447730586L;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "适用对象")
    private String[] businessActivity;

    @ApiModelProperty(value = "规则")
    private RuleBean rule;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "触发事件")
    private String event;
}
