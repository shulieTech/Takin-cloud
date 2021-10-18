package io.shulie.takin.cloud.entrypoint.report;

import java.util.List;

import io.shulie.takin.cloud.sdk.model.response.report.ReportResp;
import io.shulie.takin.cloud.sdk.model.request.report.WarnCreateReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportQueryReq;
import io.shulie.takin.cloud.sdk.model.response.report.ReportDetailResp;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.sdk.model.request.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.report.UpdateReportConclusionReq;

/**
 * @author mubai
 * @date 2020-11-02 17:02
 */
public interface CloudReportApi {

    /**
     * 列出报告
     *
     * @param req 入参
     * @return 报告列表
     */
    List<ReportResp> listReport(ReportQueryReq req);

    /**
     * 添加警告
     *
     * @param req 入参
     * @return 操作结果
     */
    String addWarn(WarnCreateReq req);

    /**
     * 更新报告状态，用于漏数检查
     *
     * @param req -
     * @return -
     */
    String updateReportConclusion(UpdateReportConclusionReq req);

    /**
     * 根据报告id获取报告详情
     *
     * @param req -
     * @return -
     */
    ReportDetailResp getReportByReportId(ReportDetailByIdReq req);

    /**
     * 根据场景id获取报告详情
     *
     * @param req -
     * @return -
     */
    ReportDetailResp tempReportDetail(ReportDetailBySceneIdReq req);

    /**
     * 根据租户查询报告数据
     *
     * @param req -
     * @return -
     */
    List<Long> queryListRunningReport(CloudCommonInfoWrapperReq req);

}
