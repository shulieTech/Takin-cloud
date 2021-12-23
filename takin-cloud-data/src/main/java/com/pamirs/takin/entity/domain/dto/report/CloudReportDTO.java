package com.pamirs.takin.entity.domain.dto.report;

import lombok.Data;
import lombok.EqualsAndHashCode;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;

import io.shulie.takin.cloud.data.model.mysql.ReportEntity;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CloudReportDTO extends ReportEntity {
    /**
     * 父类实例为入参的构造函数
     *
     * @param entity 父类实例
     */
    public CloudReportDTO(ReportEntity entity) {
        BeanUtil.copyProperties(entity, this);
    }

    @ApiModelProperty(value = "压测不通过的原因")
    private String errorMsg;

}
