package io.shulie.takin.cloud.sdk.impl.report;

import java.util.Map;
import java.util.List;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;

import io.shulie.takin.cloud.sdk.model.request.report.JtlDownloadReq;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.entrypoint.report.CloudReportApi;
import io.shulie.takin.cloud.sdk.service.CloudApiSenderService;
import io.shulie.takin.cloud.sdk.model.response.report.ReportResp;
import io.shulie.takin.cloud.sdk.model.request.report.TrendRequest;
import io.shulie.takin.cloud.sdk.model.request.report.WarnQueryReq;
import io.shulie.takin.cloud.sdk.model.request.report.WarnCreateReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportQueryReq;
import io.shulie.takin.cloud.sdk.model.response.report.ReportTrendResp;
import io.shulie.takin.cloud.sdk.model.response.report.ActivityResponse;
import io.shulie.takin.cloud.sdk.model.response.report.MetricesResponse;
import io.shulie.takin.cloud.sdk.model.response.report.ReportDetailResp;
import io.shulie.takin.cloud.sdk.model.response.report.ScriptNodeTreeResp;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.cloud.sdk.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.cloud.sdk.model.request.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.WarnDetailResponse;
import io.shulie.takin.cloud.sdk.model.request.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.report.UpdateReportConclusionReq;

/**
 * @author 无涯
 * @author 张天赐
 * @date 2020/12/17 1:29 下午
 */
@Service
public class CloudReportApiImpl implements CloudReportApi {
    @Resource
    CloudApiSenderService cloudApiSenderService;

    @Override
    public ResponseResult<List<ReportResp>> listReport(ReportQueryReq req) {
        ResponseResult<List<ReportResp>> result = cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_LIST),
            req, new TypeReference<ResponseResult<List<ReportResp>>>() {});
        return ResponseResult.success(result.getData(), result.getTotalNum());
    }

    @Override
    public ReportDetailResp detail(ReportDetailByIdReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_DETAIL),
            req, new TypeReference<ResponseResult<ReportDetailResp>>() {}).getData();

    }

    @Override
    public ReportTrendResp trend(ReportTrendQueryReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_TREND),
            req, new TypeReference<ResponseResult<ReportTrendResp>>() {}).getData();
    }

    @Override
    public ReportTrendResp tempTrend(ReportTrendQueryReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_TREND_TEMP),
            req, new TypeReference<ResponseResult<ReportTrendResp>>() {}).getData();
    }

    @Override
    public String addWarn(WarnCreateReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_WARN_ADD),
            req, new TypeReference<ResponseResult<String>>() {}).getData();
    }

    @Override
    public ResponseResult<List<WarnDetailResponse>> listWarn(WarnQueryReq req) {
        ResponseResult<List<WarnDetailResponse>> result = cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_WARN_LIST),
            req, new TypeReference<ResponseResult<List<WarnDetailResponse>>>() {});
        return ResponseResult.success(result.getData(), result.getTotalNum());
    }

    /**
     * 根据报告主键获取业务活动
     *
     * @param req 报告主键
     * @return 业务活动
     */
    @Override
    public List<ActivityResponse> activityByReportId(ReportDetailByIdReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_ACTIVITY_REPORT_ID),
            req, new TypeReference<ResponseResult<List<ActivityResponse>>>() {}).getData();
    }

    /**
     * 根据场景主键获取业务活动
     *
     * @param req 场景主键
     * @return 业务活动
     */
    @Override
    public List<ActivityResponse> activityBySceneId(ReportDetailBySceneIdReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_ACTIVITY_SCENE_ID),
            req, new TypeReference<ResponseResult<List<ActivityResponse>>>() {}).getData();
    }

    @Override
    public String updateReportConclusion(UpdateReportConclusionReq req) {
        return cloudApiSenderService.put(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_UPDATE_CONCLUSION),
            req, new TypeReference<ResponseResult<String>>() {}).getData();
    }

    @Override
    public ReportDetailResp getReportByReportId(ReportDetailByIdReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_DETAIL),
            req, new TypeReference<ResponseResult<ReportDetailResp>>() {}).getData();

    }

    @Override
    public ReportDetailResp tempReportDetail(ReportDetailBySceneIdReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_DETAIL_TEMP),
            req, new TypeReference<ResponseResult<ReportDetailResp>>() {}).getData();
    }

    @Override
    public List<Long> queryListRunningReport(CloudCommonInfoWrapperReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_LIST_RUNNING),
            req, new TypeReference<ResponseResult<List<Long>>>() {}).getData();
    }

    /**
     * 压测明细
     *
     * @param req 请求参数
     * @return 压测明细结果-节点树摘要
     */
    @Override
    public NodeTreeSummaryResp summary(ReportDetailByIdReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_SUMMARY),
            req, new TypeReference<ResponseResult<NodeTreeSummaryResp>>() {}).getData();
    }

    /**
     * 获取报告告警总数
     *
     * @param req 报告主键
     * @return 告警汇总信息
     */
    @Override
    public Map<String, Object> warnCount(ReportDetailByIdReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_WARN_COUNT),
            req, new TypeReference<ResponseResult<Map<String, Object>>>() {}).getData();
    }

    /**
     * 获取正在运行中的报告
     *
     * @param req 报告主键
     * @return 告警汇总信息
     */
    @Override
    public Long listRunning(ContextExt req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_ONE_RUNNING),
            req, new TypeReference<ResponseResult<Long>>() {}).getData();
    }

    /**
     * 锁定
     *
     * @param req 报告主键
     * @return 操作结果
     */
    @Override
    public Boolean lock(ReportDetailByIdReq req) {
        return cloudApiSenderService.put(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_LOCK),
            req, new TypeReference<ResponseResult<Boolean>>() {}).getData();
    }

    /**
     * 解锁
     *
     * @param req 报告主键
     * @return 操作结果
     */
    @Override
    public Boolean unlock(ReportDetailByIdReq req) {
        return cloudApiSenderService.put(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_UNLOCK),
            req, new TypeReference<ResponseResult<Boolean>>() {}).getData();
    }

    /**
     * 完成报告
     *
     * @param req 报告主键
     * @return 操作结果
     */
    @Override
    public Boolean finish(ReportDetailByIdReq req) {
        return cloudApiSenderService.put(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_FINISH),
            req, new TypeReference<ResponseResult<Boolean>>() {}).getData();
    }

    /**
     * 当前压测的所有数据
     *
     * @param req 请求
     * @return 响应
     */
    @Override
    public List<MetricesResponse> metrics(TrendRequest req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_METRICS),
            req, new TypeReference<ResponseResult<List<MetricesResponse>>>() {}).getData();
    }

    /**
     * 查询脚本节点树
     *
     * @param req 请求参数
     * @return 脚本节点数结果
     */
    @Override
    public List<ScriptNodeTreeResp> queryNodeTree(ScriptNodeTreeQueryReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_SCRIPT_NODE_TREE),
            req, new TypeReference<ResponseResult<List<ScriptNodeTreeResp>>>() {}).getData();
    }

    /**
     * 获取下载jtl下载路径
     *
     * @param req 请求参数
     *            <p>传入reportId即可</p>
     * @return 下载路径
     */
    @Override
    public String getJtlDownLoadUrl(JtlDownloadReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_GET_JTL_DOWNLOAD_URL),
            req, new TypeReference<ResponseResult<String>>() {}).getData();
    }

    @Override
    public Integer getReportStatusById(ReportDetailByIdReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_STATUS_BY_ID),
                req, new TypeReference<ResponseResult<Integer>>() {}).getData();

    }
}
