package io.shulie.takin.cloud.sdk.impl.report;

import java.util.List;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;

import org.springframework.stereotype.Component;
import io.shulie.takin.cloud.sdk.resp.report.ReportResp;
import io.shulie.takin.cloud.sdk.req.report.WarnCreateReq;
import io.shulie.takin.cloud.sdk.api.report.CloudReportApi;
import io.shulie.takin.cloud.sdk.req.report.ReportQueryReq;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.sdk.constant.CloudApiConstant;
import io.shulie.takin.cloud.sdk.resp.report.ReportDetailResp;
import io.shulie.takin.cloud.sdk.req.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.sdk.req.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.sdk.req.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.sdk.req.report.UpdateReportConclusionReq;
import io.shulie.takin.cloud.sdk.impl.sender.CloudApiSenderService;

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
        return cloudApiSenderService.post(CloudApiConstant.REPORT_WARN_URL, req,
                new TypeReference<ResponseResult<String>>() {})
            .getData();
    }

    @Override
    public String updateReportConclusion(UpdateReportConclusionReq req) {

        return cloudApiSenderService.put(CloudApiConstant.REPORT_UPDATE_STATE_URL, req,
                new TypeReference<ResponseResult<String>>() {})
            .getData();
    }

    @Override
    public ReportDetailResp getReportByReportId(ReportDetailByIdReq req) {
        return cloudApiSenderService.get(CloudApiConstant.REPORT_DETAIL_GET_URL, req,
                new TypeReference<ResponseResult<ReportDetailResp>>() {})
            .getData();

    }

    @Override
    public ReportDetailResp tempReportDetail(ReportDetailBySceneIdReq req) {
        return cloudApiSenderService.get(CloudApiConstant.REPORT_TEMP_DETAIL_GET_URL, req,
                new TypeReference<ResponseResult<ReportDetailResp>>() {})
            .getData();
    }

    @Override
    public List<Long> queryListRunningReport(CloudCommonInfoWrapperReq req) {
        return cloudApiSenderService.get(CloudApiConstant.REPORT_RUNNING_LIST_GET_URL, req,
                new TypeReference<ResponseResult<List<Long>>>() {})
            .getData();
    }
}
