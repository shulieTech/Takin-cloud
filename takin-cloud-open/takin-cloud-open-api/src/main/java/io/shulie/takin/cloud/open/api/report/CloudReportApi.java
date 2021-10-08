package io.shulie.takin.cloud.open.api.report;

import java.util.List;

import io.shulie.takin.cloud.open.req.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.open.req.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.open.req.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.open.req.report.ReportQueryReq;
import io.shulie.takin.cloud.open.req.report.UpdateReportConclusionReq;
import io.shulie.takin.cloud.open.req.report.WarnCreateReq;
import io.shulie.takin.cloud.open.resp.report.ReportDetailResp;
import io.shulie.takin.cloud.open.resp.report.ReportResp;
import io.shulie.takin.common.beans.response.ResponseResult;

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
    ResponseResult<List<ReportResp>> listReport(ReportQueryReq req);

    /**
     * 添加警告
     *
     * @param req 入参
     * @return 操作结果
     */
    ResponseResult<String> addWarn(WarnCreateReq req);

    /**
     * 更新报告状态，用于漏数检查
     *
     * @param req -
     * @return -
     */
    ResponseResult<String> updateReportConclusion(UpdateReportConclusionReq req);

    /**
     * 根据报告id获取报告详情
     *
     * @param req -
     * @return -
     */
    ResponseResult<ReportDetailResp> getReportByReportId(ReportDetailByIdReq req);

    /**
     * 根据场景id获取报告详情
     *
     * @param req -
     * @return -
     */
    ResponseResult<ReportDetailResp> tempReportDetail(ReportDetailBySceneIdReq req);

    /**
     * 根据租户查询报告数据
     * @param req
     * @return
     */
    ResponseResult<List<Long>> queryListRunningReport(CloudCommonInfoWrapperReq req);

}
