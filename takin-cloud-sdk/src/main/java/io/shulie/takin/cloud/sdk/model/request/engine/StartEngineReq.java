package io.shulie.takin.cloud.sdk.model.request.engine;

import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
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
    private PressureSceneEnum type;

    @ApiModelProperty(value = "业务活动配置")
    @NotEmpty(message = "业务活动配置不能为空")
    private List<SceneBusinessActivityRefOpen> businessActivityConfig;

    @ApiModelProperty(name = "uploadFile", value = "压测脚本/文件")
    @NotEmpty(message = "压测脚本/文件不能为空")
    private List<SceneScriptRefOpen> uploadFile;

    @ApiModelProperty(value = "关联到的插件列表")
    private List<EnginePluginsRefOpen> enginePlugins;

    @ApiModelProperty(value = "脚本id")
    private Long scriptId;

    @ApiModelProperty(value = "脚本发布记录id")
    private Long scriptDeployId;

    @ApiModelProperty(value = "脚本节点信息")
    private String scriptAnalysisResult;

    /**
     * 定时器周期，单位：毫秒
     */
    @ApiModelProperty(value = "定时器周期，单位：毫秒")
    private Long fixTimer;

}
