package io.shulie.takin.cloud.sdk.model.request.scenemanage;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/11/4 下午4:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTaskStartReq extends ContextExt {

    private Long sceneId;

    /**
     * cloud的统一接收参数
     */
    private String resourceName;

    private List<Long> enginePluginIds;

    private Boolean leakSqlEnable;

    private Boolean continueRead = false;

}