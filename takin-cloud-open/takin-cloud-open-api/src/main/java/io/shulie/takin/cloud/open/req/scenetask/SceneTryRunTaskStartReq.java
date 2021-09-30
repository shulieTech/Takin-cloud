package io.shulie.takin.cloud.open.req.scenetask;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.shulie.takin.ext.content.trace.ContextExt;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.shulie.takin.cloud.open.req.engine.EnginePluginsRefOpen;
import io.shulie.takin.cloud.open.req.scenemanage.SceneBusinessActivityRefOpen;
import io.shulie.takin.cloud.open.req.scenemanage.SceneScriptRefOpen;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xr.l
 * @date 2021-05-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTryRunTaskStartReq extends ContextExt {

    @ApiModelProperty(value = "业务活动配置")
    @NotEmpty(message = "业务活动配置不能为空")
    private List<SceneBusinessActivityRefOpen> businessActivityConfig;

    @ApiModelProperty(value = "脚本类型")
    @NotNull(message = "脚本类型不能为空")
    private Integer scriptType;

    @ApiModelProperty(name = "uploadFile", value = "压测脚本/文件")
    @NotEmpty(message = "压测脚本/文件不能为空")
    private List<SceneScriptRefOpen> uploadFile;

    @ApiModelProperty(value = "关联到的插件id列表")
    private List<Long> enginePluginIds;

    @ApiModelProperty(value = "关联到的插件列表")
    private List<EnginePluginsRefOpen> enginePlugins;

    @ApiModelProperty(value = "扩展字段")
    private String features;

    @ApiModelProperty(value = "脚本ID")
    private Long scriptId;

    @ApiModelProperty(value = "试跑流量条数")
    private Integer loopsNum;

    @ApiModelProperty(value = "并发数")
    private Integer concurrencyNum;
}
