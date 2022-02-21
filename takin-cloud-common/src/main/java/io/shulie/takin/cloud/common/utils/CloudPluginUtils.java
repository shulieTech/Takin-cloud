package io.shulie.takin.cloud.common.utils;

import javax.annotation.Resource;
import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.shulie.takin.cloud.ext.api.DataTraceExtApi;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.plugin.framework.core.PluginManager;

/**
 * @author hezhongqi
 * @author 张天赐
 * @date 2021/8/4 14:42
 */
@Component
public class CloudPluginUtils {
    @Resource(type = PluginManager.class)
    private PluginManager autoPluginManager;

    private static DataTraceExtApi userApi;

    @PostConstruct
    public void init() {
        userApi = autoPluginManager.getExtension(DataTraceExtApi.class);
    }

    /**
     * 返回用户id
     *
     * @return -
     */
    public static ContextExt getContext() {
        if (userApi != null) {return userApi.getContext();}
        return new ContextExt() {{
            setUserId(-1L);
            setTenantId(1L);
            setFilterSql("");
            setEnvCode("test");
            setTenantCode("default");
        }};
    }

    /**
     * 用户主键
     *
     * @return -
     */
    public static Long getUserId() {
        return getContext().getUserId();
    }

    /**
     * 租户主键
     *
     * @return -
     */
    public static Long getTenantId() {
        return getContext().getTenantId();
    }

    /**
     * 环境编码
     *
     * @return -
     */
    public static String getEnvCode() {
        return getContext().getEnvCode();
    }

    /**
     * 返回过滤sql
     *
     * @return -
     */
    public static String getFilterSql() {
        return getContext().getFilterSql();
    }

    /**
     * 公共补充 查询 用户数据
     *
     * @param ext -
     */
    public static void fillUserData(ContextExt ext) {
        ext.setUserId(getContext().getUserId());
        ext.setTenantId(getContext().getTenantId());
        ext.setEnvCode(getContext().getEnvCode());
        ext.setFilterSql(getContext().getFilterSql());
    }
}
