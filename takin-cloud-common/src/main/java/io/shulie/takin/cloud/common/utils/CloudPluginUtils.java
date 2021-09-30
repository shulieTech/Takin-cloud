package io.shulie.takin.cloud.common.utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.google.common.collect.Maps;
import io.shulie.takin.ext.api.DataTraceExtApi;
import io.shulie.takin.ext.content.trace.ContextExt;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.shulie.takin.plugin.framework.core.PluginManager;
import org.springframework.stereotype.Component;

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
     * 是否带数据溯源模块
     *
     * @return true/false
     */
    public static Boolean checkUserData() {
        return userApi != null;
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
            setTenantId(-1L);
            setEnvCode("");
            setFilterSql("");
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
    }
}
