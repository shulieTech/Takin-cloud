package io.shulie.takin.ext.api;

import java.util.List;
import java.math.BigDecimal;

import io.shulie.takin.ext.content.asset.*;
import io.shulie.takin.ext.content.response.Response;
import io.shulie.takin.plugin.framework.core.extension.ExtensionPoint;

/**
 * 资产拓展模块
 * <p>
 * 拓展接口
 *
 * @author 张天赐
 */
public interface AssetExtApi extends ExtensionPoint {

    /**
     * 冻结账户余额
     *
     * @param invoice 付款单
     * @return 返回冻结记录ID
     */
    Response<String> lock(AssetInvoiceExt<List<AssetBillExt>> invoice);

    /**
     * 释放账户余额
     * @param lockId 冻结资金记录ID,lock的返回值
     */
    boolean unlock(String lockId, Long customerId);

    /**
     * 释放账户余额
     *
     * @param customerId     租户ID
     * @param outerId 外部交易资金流水编号
     */
    boolean unlock(Long customerId, String outerId);

    /**
     * 付款
     *
     * @param invoice 付款单
     * @return 实付资产量
     */
    Response<BigDecimal> payment(AssetInvoiceExt<RealAssectBillExt> invoice);

    /**
     * 计算预估金额
     *
     * @param bill 业务信息
     * @return 预估金额
     */
    Response<BigDecimal> calcEstimateAmount(AssetBillExt bill);
    /**
     * 计算预估金额
     *
     * @param bills 业务信息
     * @return 预估金额
     */
    Response<BigDecimal> calcEstimateAmount(List<AssetBillExt> bills);

    /**
     * 查询账户信息
     *
     * @param customerId 组户ID
     * @param operateId 操作者id
     * @return 账户信息集合
     */
    AccountInfoExt queryAccount(Long customerId, Long operateId);
    List<AccountInfoExt> queryAccountByCustomerIds(Long customerId);

    /**
     * 初始化用户资产
     * @param uid    用户id
     */
    void init(Long uid);

    /**
     * 脚本调试回写流量账户
     * @param balanceExt 入参
     */
    void writeBalance(AssetBalanceExt balanceExt);
}