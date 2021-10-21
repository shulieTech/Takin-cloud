package io.shulie.takin.cloud.ext.content.trace;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.common.beans.page.PagingDevice;

/**
 * 集成了{@link PagingDevice}的{@link ContextExt}
 *
 * @author 张天赐
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PagingContextExt extends PagingDevice {
    /**
     * 用户主键
     */
    Long userId;
    /**
     * 环境编码
     */
    String envCode;
    /**
     * 租户主键
     */
    Long tenantId;
    /**
     * SQL过滤标识
     */
    String filterSql;
}
