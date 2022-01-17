package io.shulie.takin.cloud.sdk.model.request.pressure;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.sdk.model.request.engine.EnginePluginsRefOpen;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneBusinessActivityRefOpen;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneScriptRefOpen;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: liyuanba
 * @Date: 2021/12/24 2:25 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StartEngineReq extends ContextExt {
    @ApiModelProperty(value = "场景ID")
    private Long id;

    @ApiModelProperty(value = "压测场景类型")
    @NotNull(message = "压测场景类型不能为空")
    private Integer sceneType;

    @ApiModelProperty(value = "业务活动配置")
    private List<SceneBusinessActivityRefOpen> businessActivityConfig;

    @ApiModelProperty(name = "uploadFiles", value = "压测脚本/文件")
    @NotEmpty(message = "压测脚本/文件不能为空")
    private List<SceneScriptRefOpen> uploadFiles;

    @ApiModelProperty(value = "关联到的插件列表")
    private List<EnginePluginsRefOpen> enginePlugins;

    @ApiModelProperty(value = "是否从上传压测的结束的位置开始读")
    private Boolean continueRead;

    @ApiModelProperty(value = "脚本id")
    private Long scriptId;

    @ApiModelProperty(value = "脚本发布记录id")
    private Long scriptDeployId;

    @ApiModelProperty(value = "脚本节点信息")
    private String scriptNodes;

    @ApiModelProperty(value = "启动的pod数量，默认1")
    private Integer podNum;

    @ApiModelProperty(value = "压测时长")
    private Long holdTime;

    @ApiModelProperty(value = "压测时长单位：枚举TimeUnitEnum的value值，如s表示秒，m表示分")
    private String holdTimeUnit;

    @ApiModelProperty(value = "并发线程数")
    private Integer throughput;

    @ApiModelProperty(value = "巡检间隔时间，单位：毫秒")
    private Long fixTimer;

    @ApiModelProperty(value = "循环次数")
    private Long loopsNum;

}
