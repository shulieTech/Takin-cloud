package io.shulie.takin.cloud.web.entrypoint.request.scenemanage;

import java.util.List;

import io.shulie.takin.cloud.biz.input.scenemanage.EnginePluginInput;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/11/4 下午4:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTaskStartRequest extends CloudUserCommonRequestExt {
    private static final long serialVersionUID = -508486534071711694L;

    private Long sceneId;

    private List<Long> enginePluginIds;

    private List<EnginePluginInput> enginePlugins;

    private Boolean continueRead;

    /**
     *  来源名称。压测报告取场景名称、流量验证取业务活动名称、脚本调试取脚本名称
     */
    private String resourceName;
}
