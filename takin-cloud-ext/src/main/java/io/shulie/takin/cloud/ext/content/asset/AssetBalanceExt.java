package io.shulie.takin.cloud.ext.content.asset;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.swagger.annotations.ApiModelProperty;

import io.shulie.takin.cloud.ext.content.user.CloudUserExt;

/**
 * @author caijianying
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AssetBalanceExt extends CloudUserExt {
    @ApiModelProperty("cloud场景ID")
    private Long sceneId;
    @ApiModelProperty("cloud压测报告ID")
    private Long reportId;
    @ApiModelProperty("脚本调试ID")
    private Long scriptDebugId;
}
