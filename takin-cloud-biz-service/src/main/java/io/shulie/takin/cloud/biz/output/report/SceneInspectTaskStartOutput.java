package io.shulie.takin.cloud.biz.output.report;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2021/4/15 2:48 下午
 * @Description:
 */
@Data
public class SceneInspectTaskStartOutput {
    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(value = "报告ID")
    private Long reportId;

    @ApiModelProperty(value = "错误信息")
    private List<String> msg;
}
