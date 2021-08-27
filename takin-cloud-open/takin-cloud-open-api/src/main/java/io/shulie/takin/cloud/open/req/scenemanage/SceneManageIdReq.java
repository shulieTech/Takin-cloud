package io.shulie.takin.cloud.open.req.scenemanage;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.common.bean.sla.SlaBean;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @Package io.shulie.takin.cloud.open.bean.scenemanage
 * @description:
 * @date 2020/10/22 8:06 下午
 */
@Data
public class SceneManageIdReq extends CloudUserCommonRequestExt implements Serializable {

    private static final long serialVersionUID = 5258828941952507100L;

    @NotNull
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "报告id")
    private Long reportId;

    @ApiModelProperty(value = "sla触发")
    private SlaBean slaBean;
}
