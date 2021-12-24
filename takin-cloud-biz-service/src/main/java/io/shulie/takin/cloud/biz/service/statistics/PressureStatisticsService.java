package io.shulie.takin.cloud.biz.service.statistics;

import java.util.List;

import io.shulie.takin.cloud.biz.input.statistics.PressureTotalInput;
import io.shulie.takin.cloud.sdk.model.response.statistics.ReportTotalResp;
import io.shulie.takin.cloud.sdk.model.response.statistics.PressurePieTotalResp;
import io.shulie.takin.cloud.sdk.model.response.statistics.PressureListTotalResp;

/**
 * @author 无涯
 * @date 2020/11/30 9:35 下午
 */
public interface PressureStatisticsService {
    /**
     * 统计场景分类，脚本类型，返回饼状图数据
     *
     * @param input 入参
     * @return -
     */
    PressurePieTotalResp getPressurePieTotal(PressureTotalInput input);

    /**
     * 统计报告通过/未通过
     *
     * @param input 入参
     * @return -
     */
    ReportTotalResp getReportTotal(PressureTotalInput input);

    /**
     * 压测场景次数统计 && 压测脚本次数统计
     *
     * @param input 入参
     * @return -
     */
    List<PressureListTotalResp> getPressureListTotal(PressureTotalInput input);

}


