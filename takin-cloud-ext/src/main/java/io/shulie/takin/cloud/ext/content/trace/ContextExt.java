package io.shulie.takin.cloud.ext.content.trace;

import lombok.Data;

/**
 * 溯源上下文
 *
 * @author 张天赐
 */
@Data
public class ContextExt {
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