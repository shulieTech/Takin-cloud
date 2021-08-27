package io.shulie.takin.cloud.open.resp.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 公共信息返回报文
 *
 * @author lipeng
 * @date 2021-06-24 4:08 下午
 */
@Data
@ApiModel("公共信息返回报文")
public class CommonInfosResp implements Serializable {

    @ApiModelProperty("压测引擎版本号")
    private String pressureEngineVersion;

    @ApiModelProperty("cloud版本号")
    private String cloudVersion;
}
