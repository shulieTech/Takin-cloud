package io.shulie.takin.cloud.sdk.model.response.report;

import java.util.List;

import lombok.Data;

import io.swagger.annotations.ApiModelProperty;

/**
 * TODO
 *
 * @author 张天赐
 */
@Data
public class TrendResponse {

    @ApiModelProperty(value = "刻度，时间")
    private List<String> time;

    @ApiModelProperty(value = "tps")
    private List<String> tps;

    @ApiModelProperty(value = "rt")
    private List<String> rt;

    @ApiModelProperty(value = "成功率")
    private List<String> successRate;

    @ApiModelProperty(value = "sa")
    private List<String> sa;

    @ApiModelProperty(value = "并发数")
    private List<String> concurrent;
}
