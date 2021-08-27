package io.shulie.takin.cloud.open.resp.scenetask;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName:    SceneJobStateResp
 * Package:    io.shulie.takin.cloud.open.resp.scenetask
 * Description:
 * Datetime:    2021/6/23   下午5:22
 * Author:   chenhongqiao@shulie.com
 */
@Data
@ApiModel("压测任务状态返回值")
public class SceneJobStateResp implements Serializable {
    private static final long serialVersionUID = -8246217201102669377L;
    /**
     * 状态 未运行：none 运行中：running 运行失败：failed
     */
    @ApiModelProperty(value = "状态值")
    private String state;

    @ApiModelProperty(value = "描述信息")
    private String msg;
}
