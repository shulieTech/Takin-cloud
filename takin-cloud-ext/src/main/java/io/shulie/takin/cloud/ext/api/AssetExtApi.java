package io.shulie.takin.cloud.ext.api;

import java.math.BigDecimal;
import java.util.List;

import io.shulie.takin.cloud.ext.content.asset.AccountInfoExt;
import io.shulie.takin.cloud.ext.content.asset.AssetBalanceExt;
import io.shulie.takin.cloud.ext.content.asset.AssetBillExt;
import io.shulie.takin.cloud.ext.content.asset.AssetInvoiceExt;
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
     */
    boolean lock(AssetInvoiceExt invoice);

    /**
     * 释放账户余额
     *
     * @param uid     用户主键
     * @param outerId 外部交易资金流水编号
     */
    boolean unlock(Long uid, String outerId);

    /**
     * 付款
     *
     * @param invoice 付款单
     * @return 实付资产量
     */
    BigDecimal payment(AssetInvoiceExt invoice);

    /**
     * 计算预估金额
     *
     * @param bill 业务信息
     * @return 预估金额
     */
    BigDecimal calcEstimateAmount(AssetBillExt bill);

    /**
     * 计算预估金额
     *
     * @param bills 业务信息
     * @return 预估金额
     */
    BigDecimal calcEstimateAmount(List<AssetBillExt> bills);

    /**
     * 计算实际金额
     *
     * @param bill 业务信息
     * @return 实际金额
     */
    BigDecimal calcRealityAmount(AssetBillExt bill);

    /**
     * 查询账户信息
     *
     * @param customerId 组户ID
     * @param operateId  操作者id
     * @return 账户信息集合
     */
    AccountInfoExt queryAccount(Long customerId, Long operateId);

    /**
     * 查询账户信息
     *
     * @param userIdList 用户主键集合
     * @return 账户信息集合
     */
    List<AccountInfoExt> queryAccountInfoByUserIds(List<Long> userIdList);

    /**
     * 初始化用户资产
     *
     * @param userId 用户主键
     */
    void init(Long userId);

    /**
     * 脚本调试回写流量账户
     *
     * @param balanceExt 入参
     */
    void writeBalance(AssetBalanceExt balanceExt);
}