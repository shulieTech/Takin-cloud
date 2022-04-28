package io.shulie.takin.cloud.app.service;

import java.util.List;

import io.shulie.takin.cloud.app.entity.SlaEntity;
import io.shulie.takin.cloud.app.entity.SlaEventEntity;
import io.shulie.takin.cloud.constant.enums.FormulaSymbol;
import io.shulie.takin.cloud.constant.enums.FormulaTarget;
import io.shulie.takin.cloud.model.notify.Metrics.MetricsInfo;

/**
 * SLA服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface SlaService {
    /**
     * 列出任务相关的所有SLA
     *
     * @param jobId 任务主键
     * @return SLA列表
     */
    List<SlaEntity> list(long jobId);

    /**
     * 创建SLA记录
     *
     * @param jobId  任务主键
     * @param ref    关键字
     * @param target 算数目标
     * @param symbol 算数符号
     * @param number 对比值
     */
    void create(long jobId, String ref, FormulaTarget target, FormulaSymbol symbol, double number);

    /**
     * 创建SLA触发记录
     * <p>入库、回调</p>
     *
     * @param slaEventEntityList 触发了SLA的记录
     */
    void event(List<SlaEventEntity> slaEventEntityList);

    /**
     * 检出触发SAL的指标数据
     *
     * @param metricsInfoList 指标数据
     * @return 触发了SLA的指标
     */
    List<SlaEventEntity> check(List<MetricsInfo> metricsInfoList);
}
