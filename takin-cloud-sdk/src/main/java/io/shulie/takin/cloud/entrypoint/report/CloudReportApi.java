package io.shulie.takin.cloud.entrypoint.report;

import java.util.List;
import java.util.Map;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.sdk.model.common.BusinessActivitySummaryBean;
import io.shulie.takin.cloud.sdk.model.request.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportQueryReq;
import io.shulie.takin.cloud.sdk.model.request.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.cloud.sdk.model.request.report.TrendRequest;
import io.shulie.takin.cloud.sdk.model.request.report.UpdateReportConclusionReq;
import io.shulie.takin.cloud.sdk.model.request.report.WarnCreateReq;
import io.shulie.takin.cloud.sdk.model.request.report.WarnQueryReq;
import io.shulie.takin.cloud.sdk.model.response.report.ActivityResponse;
import io.shulie.takin.cloud.sdk.model.response.report.MetricesResponse;
import io.shulie.takin.cloud.sdk.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.cloud.sdk.model.response.report.ReportDetailResp;
import io.shulie.takin.cloud.sdk.model.response.report.ReportResp;
import io.shulie.takin.cloud.sdk.model.response.report.ScriptNodeTreeResp;
import io.shulie.takin.cloud.sdk.model.response.report.TrendResponse;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.WarnDetailResponse;
import io.shulie.takin.common.beans.response.ResponseResult;

/**
 * @author mubai
 * @date 2020-11-02 17:02
 */
@SuppressWarnings("unused")
public interface CloudReportApi {

    /**
     * 列出报告
     *
     * @param req 入参
     * @return 报告列表
     */
    ResponseResult<List<ReportResp>> listReport(ReportQueryReq req);

    /**
     * 报告详情
     *
     * @param req 报告主键
     * @return 报告详情
     */
    ReportDetailResp detail(ReportDetailByIdReq req);

    /**
     * 报告趋势
     *
     * @param req 请求入参
     * @return 出参
     */
    TrendResponse trend(TrendRequest req);

    /**
     * 临时报告趋势
     *
     * @param req 请求入参
     * @return 出参
     */
    TrendResponse tempTrend(TrendRequest req);

    /**
     * 添加警告
     *
     * @param req 入参
     * @return 操作结果
     */
    String addWarn(WarnCreateReq req);

    /**
     * 列出告警
     *
     * @param req 请求信息
     * @return 告警集合
     */
    ResponseResult<List<WarnDetailResponse>> listWarn(WarnQueryReq req);

    /**
     * 根据报告主键获取业务活动
     *
     * @param req 报告主键
     * @return 业务活动
     */
    List<ActivityResponse> activityByReportId(ReportDetailByIdReq req);

    /**
     * 根据场景主键获取业务活动
     *
     * @param req 场景主键
     * @return 业务活动
     */
    List<ActivityResponse> activityBySceneId(ReportDetailBySceneIdReq req);

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

    /**
     * 获取压测总结
     *
     * @param req 压测主键
     * @return 总结信息
     */
    List<BusinessActivitySummaryBean> summary(ReportDetailByIdReq req);

    /**
     * 获取报告告警总数
     *
     * @param req 报告主键
     * @return 告警汇总信息
     */
    Map<String, Object> warnCount(ReportDetailByIdReq req);

    /**
     * 获取正在运行中的报告
     *
     * @param req 报告主键
     * @return 告警汇总信息
     */
    Long listRunning(ContextExt req);

    /**
     * 锁定
     *
     * @param req 报告主键
     * @return 操作结果
     */
    Boolean lock(ReportDetailByIdReq req);

    /**
     * 解锁
     *
     * @param req 报告主键
     * @return 操作结果
     */
    Boolean unlock(ReportDetailByIdReq req);

    /**
     * 完成报告
     *
     * @param req 报告主键
     * @return 操作结果
     */
    Boolean finish(ReportDetailByIdReq req);

    /**
     * 当前压测的所有数据
     *
     * @param req 请求
     * @return 响应
     */
    List<MetricesResponse> metrics(TrendRequest req);

    /**
     * 查询脚本节点树
     *
     * @param req 请求参数
     * @return 脚本节点响应
     */
    ResponseResult<List<ScriptNodeTreeResp>> queryNodeTree(ScriptNodeTreeQueryReq req);

    /**
     * 压测明细
     *
     * @param req 请求参数
     * @return 节点树汇总响应
     */
    ResponseResult<NodeTreeSummaryResp> getSummaryList(ReportDetailByIdReq req);
}
