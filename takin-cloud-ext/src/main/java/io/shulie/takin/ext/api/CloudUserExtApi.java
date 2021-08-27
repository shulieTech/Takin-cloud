package io.shulie.takin.ext.api;

import java.util.List;
import java.util.Map;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.shulie.takin.ext.content.user.CloudUserExt;
import io.shulie.takin.plugin.framework.core.extension.ExtensionPoint;

/**
 * @author hezhongqi
 * @Package io.shulie.takin.web.plugin.api.user
 * @ClassName: UserPluginApi
 * @description:
 * @date 2021/7/29 20:30
 */
public interface CloudUserExtApi extends ExtensionPoint {

    /**
     * 填充用户数据 简单补充用户数据
     * 用于：
     * 1.mybatis 拦截补充用户数据，插入 + 更新
     * 2.补充查询条件 用户数据
     * @param requestExt
     */
    void fillUserData(CloudUserCommonRequestExt requestExt);

    /**
     * 根据租户ids 查询数据
     * todo 后续想办法废弃
     * @param customerIds
     * @return
     */
    Map<Long, String> getUserNameMap(List<Long> customerIds);

    /**
     * 获取登录用户信息
     * @return
     */
    CloudUserExt getUser();

    /**
     * 获取web -> cloud 请求的参数数据
     * filterSql
     * customerId
     * userIds
     * @return
     */
    CloudUserCommonRequestExt getRequestExt();

    /**
     *补充租户名
     * @param sourceExt
     * @param targetExt
     */
    void fillCustomerName(CloudUserCommonRequestExt sourceExt, CloudUserCommonRequestExt targetExt);

    /**
     * 补充报告信息
     * 一个接口两个作用 报告接口
     * 1.报告补充查询条件
     * 2.报告补充操作人
     * @param reportExt
     * @param targetExt
     */
    void fillReportData(CloudUserCommonRequestExt reportExt, CloudUserCommonRequestExt targetExt);

    /**
     * 判断用户版本
     * @param reportExt
     * @return
     */
    Boolean checkVersion(CloudUserCommonRequestExt reportExt);

}
