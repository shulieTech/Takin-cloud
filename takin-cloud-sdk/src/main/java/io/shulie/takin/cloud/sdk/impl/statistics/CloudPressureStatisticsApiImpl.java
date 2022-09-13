package io.shulie.takin.cloud.sdk.impl.statistics;

import java.util.List;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;

import io.shulie.takin.cloud.sdk.model.request.statistics.FullRequest;
import io.shulie.takin.cloud.sdk.model.response.statistics.FullResponse;
import io.shulie.takin.cloud.entrypoint.statistics.CloudPressureStatisticsApi;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.sdk.model.request.statistics.PressureTotalReq;
import io.shulie.takin.cloud.sdk.model.response.statistics.PressureListTotalResp;
import io.shulie.takin.cloud.sdk.model.response.statistics.PressurePieTotalResp;
import io.shulie.takin.cloud.sdk.model.response.statistics.ReportTotalResp;
import io.shulie.takin.cloud.sdk.service.CloudApiSenderService;
import io.shulie.takin.common.beans.response.ResponseResult;
import org.springframework.stereotype.Service;

/**
 * @author 无涯
 * @date 2020/11/30 9:53 下午
 */
@Service
public class CloudPressureStatisticsApiImpl implements CloudPressureStatisticsApi {

    @Resource
    CloudApiSenderService cloudApiSenderService;

    @Override
    public PressurePieTotalResp getPressurePieTotal(PressureTotalReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_STATISTICS, EntrypointUrl.METHOD_STATISTICS_PRESSURE_PIE_TOTAL),
            req, new TypeReference<ResponseResult<PressurePieTotalResp>>() {}).getData();
    }

    @Override
    public ReportTotalResp getReportTotal(PressureTotalReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_STATISTICS, EntrypointUrl.METHOD_STATISTICS_REPORT_TOTAL),
            req, new TypeReference<ResponseResult<ReportTotalResp>>() {}).getData();
    }

    @Override
    public List<PressureListTotalResp> getPressureListTotal(PressureTotalReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_STATISTICS, EntrypointUrl.METHOD_STATISTICS_PRESSURE_LIST_TOTAL),
            req, new TypeReference<ResponseResult<List<PressureListTotalResp>>>() {}).getData();
    }

    @Override
    public FullResponse full(FullRequest req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_STATISTICS, EntrypointUrl.METHOD_STATISTICS_PRESSURE_FULL),
            req, new TypeReference<ResponseResult<FullResponse>>() {}).getData();
    }

}
