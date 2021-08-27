package io.shulie.takin.cloud.biz.output.scenetask;

import java.io.Serializable;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @author xr.l
 * @date 2021-05-10
 */
@Data
public class SceneTryRunTaskStartOutput extends CloudUserCommonRequestExt implements Serializable {
    private static final long serialVersionUID = -7691499995105603644L;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 报告ID
     */
    private Long reportId;


}
