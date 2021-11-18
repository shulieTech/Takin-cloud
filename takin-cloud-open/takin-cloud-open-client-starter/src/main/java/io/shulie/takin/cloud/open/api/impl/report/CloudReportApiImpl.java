package io.shulie.takin.cloud.open.api.impl.report;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import io.shulie.takin.cloud.open.api.impl.CloudCommonApi;
import io.shulie.takin.cloud.open.api.report.CloudReportApi;
import io.shulie.takin.cloud.open.constant.CloudApiConstant;
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
import io.shulie.takin.utils.http.HttpHelper;
import io.shulie.takin.utils.http.TakinResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.tro.properties.TroCloudClientProperties;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2020/12/17 1:29 下午
 */
@Component
public class CloudReportApiImpl extends CloudCommonApi implements CloudReportApi {
    @Autowired
    private TroCloudClientProperties troCloudClientProperties;

    @Override
    public ResponseResult<List<ReportResp>> listReport(ReportQueryReq req) {
        return null;
    }

    @Override
    public ResponseResult<String> addWarn(WarnCreateReq req) {
        TakinResponseEntity<ResponseResult<String>> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.REPORT_WARN_URL,
                getHeaders(req), new TypeReference<ResponseResult<String>>() {}, req);
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<String> updateReportConclusion(UpdateReportConclusionReq req) {
        TakinResponseEntity<ResponseResult<String>> takinResponseEntity =
            HttpHelper.doPut(troCloudClientProperties.getUrl() + CloudApiConstant.REPORT_UPDATE_STATE_URL,
                getHeaders(req), new TypeReference<ResponseResult<String>>() {}, req);
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<ReportDetailResp> getReportByReportId(ReportDetailByIdReq req) {
        TakinResponseEntity<ResponseResult<ReportDetailResp>> takinResponseEntity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.REPORT_DETAIL_GET_URL,
                getHeaders(req), req, new TypeReference<ResponseResult<ReportDetailResp>>() {});
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<ReportDetailResp> tempReportDetail(ReportDetailBySceneIdReq req) {
        TakinResponseEntity<ResponseResult<ReportDetailResp>> takinResponseEntity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.REPORT_TEMP_DETAIL_GET_URL,
                getHeaders(req), req, new TypeReference<ResponseResult<ReportDetailResp>>() {});
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<List<ScriptNodeTreeResp>> queryNodeTree(ScriptNodeTreeQueryReq req) {
        TakinResponseEntity<ResponseResult<List<ScriptNodeTreeResp>>> entity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.SCRIPT_NODE_TREE,
                getHeaders(req), req, new TypeReference<ResponseResult<List<ScriptNodeTreeResp>>>() {});
        if (entity.getSuccess()){
            return entity.getBody();
        }
       return ResponseResult.fail(entity.getHttpStatus().toString(),
           entity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<ReportTrendResp> queryTempReportTrend(ReportTrendQueryReq req) {
        TakinResponseEntity<ResponseResult<ReportTrendResp>> entity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.REPORT_TEMP_TREND,
                getHeaders(req), req, new TypeReference<ResponseResult<ReportTrendResp>>() {});
        if (entity.getSuccess()){
            return entity.getBody();
        }
        return ResponseResult.fail(entity.getHttpStatus().toString(),
            entity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<ReportTrendResp> queryReportTrend(ReportTrendQueryReq req) {
        TakinResponseEntity<ResponseResult<ReportTrendResp>> entity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.REPORT_TREND,
                getHeaders(req), req, new TypeReference<ResponseResult<ReportTrendResp>>() {});
        if (entity.getSuccess()){
            return entity.getBody();
        }
        return ResponseResult.fail(entity.getHttpStatus().toString(),
            entity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<NodeTreeSummaryResp> getSummaryList(ReportDetailByIdReq req) {
        TakinResponseEntity<ResponseResult<NodeTreeSummaryResp>> entity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.REPORT_SUMMARY_LIST,
                getHeaders(req), req, new TypeReference<ResponseResult<NodeTreeSummaryResp>>() {});
        if (entity.getSuccess()){
            return entity.getBody();
        }
        return ResponseResult.fail(entity.getHttpStatus().toString(),
            entity.getErrorMsg(), "查看cloud日志");
    }
}
