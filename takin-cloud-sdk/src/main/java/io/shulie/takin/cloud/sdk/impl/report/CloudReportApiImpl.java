package io.shulie.takin.cloud.sdk.impl.report;

import java.util.List;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;

import org.springframework.stereotype.Component;

import io.shulie.takin.cloud.sdk.service.CloudApiSenderService;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.entrypoint.report.CloudReportApi;
import io.shulie.takin.cloud.sdk.model.response.report.ReportResp;
import io.shulie.takin.cloud.sdk.model.request.report.WarnCreateReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportQueryReq;
import io.shulie.takin.cloud.sdk.model.response.report.ReportDetailResp;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.sdk.model.request.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.report.UpdateReportConclusionReq;

/**
 * @author 无涯
 * @author 张天赐
 * @date 2020/12/17 1:29 下午
 */
@Component
public class CloudReportApiImpl implements CloudReportApi {
    @Resource
    CloudApiSenderService cloudApiSenderService;

    @Override
    public List<ReportResp> listReport(ReportQueryReq req) {
        return null;
    }

    @Override
    public String addWarn(WarnCreateReq req) {
        return cloudApiSenderService.post(
            EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_REPORT_WARN_ADD),
            req, new TypeReference<ResponseResult<String>>() {}).getData();
    }

    @Override
    public String updateReportConclusion(UpdateReportConclusionReq req) {

        return cloudApiSenderService.put(
            EntrypointUrl.join(EntrypointUrl.MODULE_FILE, EntrypointUrl.METHOD_REPORT_UPDATE_CONCLUSION),
            req, new TypeReference<ResponseResult<String>>() {}).getData();
    }

    @Override
    public ReportDetailResp getReportByReportId(ReportDetailByIdReq req) {
        return cloudApiSenderService.get(
            EntrypointUrl.join(EntrypointUrl.MODULE_FILE, EntrypointUrl.METHOD_REPORT_DETAIL),
            req, new TypeReference<ResponseResult<ReportDetailResp>>() {}).getData();

    }

    @Override
    public ReportDetailResp tempReportDetail(ReportDetailBySceneIdReq req) {
        return cloudApiSenderService.get(
            EntrypointUrl.join(EntrypointUrl.MODULE_FILE, EntrypointUrl.METHOD_REPORT_DETAIL_TEMP),
            req, new TypeReference<ResponseResult<ReportDetailResp>>() {}).getData();
    }

    @Override
    public List<Long> queryListRunningReport(CloudCommonInfoWrapperReq req) {
        return cloudApiSenderService.get(
            EntrypointUrl.join(EntrypointUrl.MODULE_FILE, EntrypointUrl.METHOD_REPORT_LIST_RUNNING),
            req, new TypeReference<ResponseResult<List<Long>>>() {}).getData();
    }
}
