package io.shulie.takin.cloud.open.api.impl.statistics;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import io.shulie.takin.cloud.open.api.impl.CloudCommonApi;
import io.shulie.takin.cloud.open.api.statistics.CloudPressureStatisticsApi;
import io.shulie.takin.cloud.open.constant.CloudApiConstant;
import io.shulie.takin.cloud.open.req.statistics.PressureTotalReq;
import io.shulie.takin.cloud.open.resp.statistics.PressureListTotalResp;
import io.shulie.takin.cloud.open.resp.statistics.PressurePieTotalResp;
import io.shulie.takin.cloud.open.resp.statistics.ReportTotalResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.http.HttpHelper;
import io.shulie.takin.utils.http.TakinResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.tro.properties.TroCloudClientProperties;
import org.springframework.stereotype.Service;

/**
 * @author 无涯
 * @Package io.shulie.takin.web.diff.api.statistics
 * @date 2020/11/30 9:53 下午
 */
@Service
public class CloudPressureStatisticsApiImpl extends CloudCommonApi implements CloudPressureStatisticsApi {

    @Autowired
    private TroCloudClientProperties troCloudClientProperties;


    @Override
    public ResponseResult<PressurePieTotalResp> getPressurePieTotal(PressureTotalReq req) {
        TakinResponseEntity<ResponseResult<PressurePieTotalResp>> takinResponseEntity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.STATISTIC_PRESSUREPIE_URL,
                getHeaders(req),req,new TypeReference<ResponseResult<PressurePieTotalResp>>() {});
        if(takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.success();
    }

    @Override
    public ResponseResult<ReportTotalResp> getReportTotal(PressureTotalReq req) {
        TakinResponseEntity<ResponseResult<ReportTotalResp>> takinResponseEntity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.STATISTIC_REPORT_URL,
                getHeaders(req),req,new TypeReference<ResponseResult<ReportTotalResp>>() {});
        if(takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.success();
    }

    @Override
    public ResponseResult<List<PressureListTotalResp>> getPressureListTotal(PressureTotalReq req) {
        TakinResponseEntity<ResponseResult<List<PressureListTotalResp>>> takinResponseEntity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.STATISTIC_PRESSURELIST_URL,
                getHeaders(req),req,new TypeReference<ResponseResult<List<PressureListTotalResp>>>() {});
        if(takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.success();
    }

}
