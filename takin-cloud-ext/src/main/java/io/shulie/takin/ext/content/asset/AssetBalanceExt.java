package io.shulie.takin.ext.content.asset;

import java.io.Serializable;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author caijianying
 */
@Data
public class AssetBalanceExt extends CloudUserCommonRequestExt implements Serializable {
    @ApiModelProperty("cloud场景ID")
    private Long sceneId;
    @ApiModelProperty("cloud压测报告ID")
    private Long reportId;
    @ApiModelProperty("脚本调试ID")
    private Long scriptDebugId;
}
