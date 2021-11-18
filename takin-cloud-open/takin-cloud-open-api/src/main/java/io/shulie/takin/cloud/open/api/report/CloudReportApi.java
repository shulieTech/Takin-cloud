package io.shulie.takin.cloud.open.api.report;

import java.util.List;

import io.shulie.takin.cloud.open.req.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.open.req.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.open.req.report.ReportQueryReq;
import io.shulie.takin.cloud.open.req.report.ReportTrendQueryReq;
import io.shulie.takin.cloud.open.req.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.cloud.open.req.report.UpdateReportConclusionReq;
import io.shulie.takin.cloud.open.req.report.WarnCreateReq;
import io.shulie.takin.cloud.open.resp.report.NodeTreeSummaryResp;
import io.shulie.takin.cloud.open.resp.report.ReportDetailResp;
import io.shulie.takin.cloud.open.resp.report.ReportResp;
import io.shulie.takin.cloud.open.resp.report.ReportTrendResp;
import io.shulie.takin.cloud.open.resp.report.ScriptNodeTreeResp;
import io.shulie.takin.common.beans.response.ResponseResult;

/**
 * @author mubai
 * @date 2020-11-02 17:02
 */
public interface CloudReportApi {

    ResponseResult<List<ReportResp>> listReport(ReportQueryReq req);

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
     *  查询脚本节点树
     * @param req 请求参数
     * @return
     */
    ResponseResult<List<ScriptNodeTreeResp>> queryNodeTree(ScriptNodeTreeQueryReq req);

    /**
     *实况报告链路趋势
     * @param req 请求参数
     * @return
     */
    ResponseResult<ReportTrendResp> queryTempReportTrend(ReportTrendQueryReq req);

    /**
     * 报告链路趋势
     * @param req 请求参数
     * @return
     */
    ResponseResult<ReportTrendResp> queryReportTrend(ReportTrendQueryReq req);

    /**
     * 压测明细
     * @param req 请求参数
     * @return
     */
    ResponseResult<NodeTreeSummaryResp> getSummaryList(ReportDetailByIdReq req);

}
