package io.shulie.takin.ext.content.user;

import java.io.Serializable;
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
public class CloudUserCommonRequestExt extends PagingDevice implements Serializable {

    private static final long serialVersionUID = -1529428936481160409L;

    /**
     * 用户字段 源字段 为 uid
     * 报告模块中 ：操作人id
     */
    @ApiModelProperty(value = "负责人ID,操作人id")
    private Long userId;
    /**
     * 租户字段
     */
    private Long customerId;

    /**
     * 租户ids
     */
    private List<Long> customerIds;

    /**
     * license  转化成 customerId
     */
    private transient String license;

    /**
     * 查询过滤sql
     */
    private transient String filterSql;

    /**
     * 对应字段 name
     */
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /**
     * 操作用户ID
     */
    private Long operateId;

    @ApiModelProperty(value = "操作人")
    private String operateName;

    /**
     * 报告模块用
     */
    @ApiModelProperty(name = "userIdStr", value = "负责人ids")
    private String userIdStr;
    private List<Long> userIds;

    private Long deptId;

}
