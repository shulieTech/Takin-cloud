package io.shulie.takin.ext.content.asset;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author caijianying
 */
@Data
public class AssetBalanceExt {
    @ApiModelProperty("cloud场景ID")
    private Long sceneId;
    @ApiModelProperty("cloud压测报告ID")
    private Long reportId;
    @ApiModelProperty("脚本调试ID")
    private Long scriptDebugId;
}
