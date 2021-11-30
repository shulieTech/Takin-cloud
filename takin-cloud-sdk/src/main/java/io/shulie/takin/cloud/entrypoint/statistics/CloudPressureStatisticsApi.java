package io.shulie.takin.cloud.entrypoint.statistics;

import java.util.List;

import io.shulie.takin.cloud.sdk.model.request.statistics.PressureTotalReq;
import io.shulie.takin.cloud.sdk.model.response.statistics.ReportTotalResp;
import io.shulie.takin.cloud.sdk.model.response.statistics.PressurePieTotalResp;
import io.shulie.takin.cloud.sdk.model.response.statistics.PressureListTotalResp;

/**
 * @author 无涯
 * @date 2020/11/30 9:53 下午
 */
public interface CloudPressureStatisticsApi {
    /**
     * 统计场景分类，脚本类型，返回饼状图数据
     *
     * @param input -
     * @return -
     */
    PressurePieTotalResp getPressurePieTotal(PressureTotalReq input);

    /**
     * 统计报告通过/未通过
     *
     * @param input -
     * @return -
     */
    ReportTotalResp getReportTotal(PressureTotalReq input);

    /**
     * 压测场景次数统计 && 压测脚本次数统计
     *
     * @param input -
     * @return -
     */
    List<PressureListTotalResp> getPressureListTotal(PressureTotalReq input);
}
