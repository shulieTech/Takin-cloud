package io.shulie.takin.cloud.common.utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Maps;
import io.shulie.takin.ext.api.CloudUserExtApi;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.shulie.takin.ext.content.user.CloudUserExt;
import io.shulie.takin.plugin.framework.core.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.ext.util
 * @ClassName: CustomUtil
 * @Description:
 * @Date: 2021/8/4 14:42
 */
public class CloudPluginUtils {



    private static CloudUserExtApi userApi;
    static PluginManager pluginManager;
    @Autowired
    public void setPluginManager(PluginManager pluginManager) {
        CloudPluginUtils.pluginManager = pluginManager;
        userApi = pluginManager.getExtension(CloudUserExtApi.class);
    }
    /**
     * 返回用户id
     * @return
     */
    public static CloudUserExt getUser() {
        if(userApi != null) {
            return userApi.getUser();
        }
       return null;
    }


    /**
     * 目前用户id = 租户id
     * @return
     */
    public static Long getCustomerId() {
        if(userApi != null) {
            if(getUser() != null) {
                return getUser().getCustomerId();
            }
        }
        // 默认给-1
        return -1L;
    }

    /**
     * 返回过滤sql
     * @return
     */
    public static String getFilterSql() {
        if(userApi != null) {
            if(userApi.getRequestExt() != null) {
                return userApi.getRequestExt().getFilterSql();
            }
        }
        return "";
    }


    /**
     * 公共补充 查询 用户数据
     * @param userCommonExt
     */
    public static void fillUserData(CloudUserCommonRequestExt userCommonExt) {
        if (Objects.nonNull(userApi)) {
            userApi.fillUserData(userCommonExt);
        } else {
            userCommonExt.setUserId(-1L);
            userCommonExt.setCustomerId(-1L);
        }
    }

    public static Boolean checkVersion(CloudUserCommonRequestExt reportExt) {
        if (Objects.nonNull(userApi)) {
            return userApi.checkVersion(reportExt);
        }
        return true;
    }

    /**
     * 报告补充 查询 报告用户数据
     * @param reportExt
     * @param targetExt
     */
    public static void fillReportData(CloudUserCommonRequestExt reportExt, CloudUserCommonRequestExt targetExt) {
        if (Objects.nonNull(userApi)) {
            userApi.fillReportData(reportExt, targetExt);
        }
    }

    /**
     * 获取用户信息
     * @param customerIds
     * @return
     */
    public static Map<Long, String> getUserNameMap(List<Long> customerIds) {
        if (Objects.nonNull(userApi)) {
            return userApi.getUserNameMap(customerIds);
        }
        return Maps.newHashMap();
    }

    public static void fillCustomerName(CloudUserCommonRequestExt sourceExt, CloudUserCommonRequestExt targetExt) {
        if (Objects.nonNull(userApi)) {
            userApi.fillCustomerName(sourceExt, targetExt);
        }
    }

    public static void fillCustomerName(CloudUserCommonRequestExt requestExt, Map<Long, String> userMap ) {
        if (!userMap.isEmpty() && Objects.nonNull(requestExt.getUserId()) && Objects.nonNull(userMap.get(requestExt.getUserId()))) {
            requestExt.setCustomerName(userMap.get(requestExt.getUserId()));
        }
    }
}
