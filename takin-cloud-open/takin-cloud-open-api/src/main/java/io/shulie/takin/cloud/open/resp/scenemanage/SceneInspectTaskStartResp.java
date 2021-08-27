package io.shulie.takin.cloud.open.resp.scenemanage;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2021/4/15 2:45 下午
 * @Description:
 */
@Data
@ApiModel("巡检场景任务启动返回值")
public class SceneInspectTaskStartResp {
    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(value = "报告ID")
    private Long reportId;

    @ApiModelProperty(value = "错误信息")
    private List<String> msg;
}
