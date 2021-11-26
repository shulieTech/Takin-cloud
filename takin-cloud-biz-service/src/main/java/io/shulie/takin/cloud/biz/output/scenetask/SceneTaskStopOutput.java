package io.shulie.takin.cloud.biz.output.scenetask;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;

import io.shulie.takin.cloud.common.pojo.AbstractEntry;

/**
 * @author liyuanba
 * @date 2021/11/26 11:40 上午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SceneTaskStopOutput extends AbstractEntry {
    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(value = "任务ID")
    private Long reportId;

    @ApiModelProperty(value = "错误信息")
    private List<String> msgs = new ArrayList<>(0);

    public void addMsg(String msg) {msgs.add(msg);}
}
