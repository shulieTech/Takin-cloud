package io.shulie.takin.cloud.data.result.scenemanage;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;

/**
 * @author qianshui
 * @date 2020/4/17 下午2:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "列表查询出参")
public class SceneManageListResult extends SceneManageEntity {

    /**
     * 父类实例为入参的构造函数
     *
     * @param entity 父类实例
     */
    public SceneManageListResult(SceneManageEntity entity) {
        BeanUtil.copyProperties(entity, this);
    }

    @ApiModelProperty(value = "是否有报告")
    private Boolean hasReport;

    @ApiModelProperty(value = "预计消耗流量")
    private BigDecimal estimateFlow;

    @ApiModelProperty(value = "最大并发")
    private Integer threadNum;
}
