package io.shulie.takin.cloud.open.resp.scenetask;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaoyong
 */
@Data
@ApiModel("状态检查返回值")
public class SceneActionResp implements Serializable {

    private static final long serialVersionUID = 5802897364685645749L;

    @ApiModelProperty(value = "状态值")
    private Long data;

    @ApiModelProperty(value = "报告ID")
    private Long reportId;

    @ApiModelProperty(value = "错误信息")
    private List<String> msg;

}
