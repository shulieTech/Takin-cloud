package io.shulie.takin.ext.content.user;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 放在这里的原因，PagingDevice需要支持
 *
 * @author hezhongqi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CloudUserCommonRequestExt extends PagingDevice {

    /**
     * 用户字段 源字段 为 uid
     * 报告模块中 ：操作人id
     */
    @ApiModelProperty(value = "负责人ID,操作人id")
    private Long userId;
    /**
     * 租户字段
     */
    private Long tenantId;

    /**
     * 查询过滤sql
     */
    private transient String filterSql;
}
