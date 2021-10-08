package io.shulie.takin.cloud.open.req.scenemanage;


import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModelProperty;
import io.shulie.takin.cloud.common.bean.sla.SlaBean;

/**
 * @author 无涯
 * @date 2020/10/22 8:06 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneManageIdReq extends ContextExt {

    @NotNull
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "报告id")
    private Long reportId;

    @ApiModelProperty(value = "sla触发")
    private SlaBean slaBean;
}
