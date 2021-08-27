package io.shulie.takin.cloud.open.resp.scenemanage;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2021/4/20 8:27 下午
 * @Description:
 */
@Data
public class SceneInspectTaskStopResp {
    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(value = "错误信息")
    private List<String> msg;
}
