package io.shulie.takin.cloud.web.entrypoint.response.scenemanage;

import java.io.Serializable;
import java.math.BigDecimal;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName SceneManageListResult
 * @Description
 * @Author qianshui
 * @Date 2020/4/17 下午2:45
 */
@Data
@ApiModel(description = "列表查询出参")
public class SceneManageListResponse extends CloudUserCommonRequestExt implements Serializable {

    private static final long serialVersionUID = -3967473117069389164L;

    @ApiModelProperty(name = "id", value = "ID")
    private Long id;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "最新压测时间")
    private String lastPtTime;

    @ApiModelProperty(value = "是否有报告")
    private Boolean hasReport;

    @ApiModelProperty(value = "预计消耗流量")
    private BigDecimal estimateFlow;

    @ApiModelProperty(value = "最大并发")
    private Integer threadNum;
}
