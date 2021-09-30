package io.shulie.takin.cloud.open.resp.scenetask;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhaoyong
 */
@Data
@ApiModel("状态检查返回值")
public class SceneActionResp {

    @ApiModelProperty(value = "状态值")
    private Long data;

    @ApiModelProperty(value = "报告ID")
    private Long reportId;

    @ApiModelProperty(value = "错误信息")
    private List<String> msg;

}
