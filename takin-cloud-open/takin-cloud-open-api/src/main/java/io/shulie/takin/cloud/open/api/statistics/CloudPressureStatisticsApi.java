package io.shulie.takin.cloud.open.api.statistics;

import java.util.List;

import io.shulie.takin.cloud.open.req.statistics.PressureTotalReq;
import io.shulie.takin.cloud.open.resp.statistics.PressureListTotalResp;
import io.shulie.takin.cloud.open.resp.statistics.PressurePieTotalResp;
import io.shulie.takin.cloud.open.resp.statistics.ReportTotalResp;
import io.shulie.takin.common.beans.response.ResponseResult;

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
    ResponseResult<PressurePieTotalResp> getPressurePieTotal(PressureTotalReq input);

    /**
     * 统计报告通过/未通过
     *
     * @param input -
     * @return -
     */
    ResponseResult<ReportTotalResp> getReportTotal(PressureTotalReq input);

    /**
     * 压测场景次数统计 && 压测脚本次数统计
     *
     * @param input -
     * @return -
     */
    ResponseResult<List<PressureListTotalResp>> getPressureListTotal(PressureTotalReq input);
}
