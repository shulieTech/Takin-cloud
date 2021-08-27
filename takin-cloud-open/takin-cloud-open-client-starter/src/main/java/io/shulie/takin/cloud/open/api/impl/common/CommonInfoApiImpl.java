package io.shulie.takin.cloud.open.api.impl.common;

import com.fasterxml.jackson.core.type.TypeReference;
import io.shulie.takin.cloud.open.api.common.CommonInfoApi;
import io.shulie.takin.cloud.open.api.impl.CloudCommonApi;
import io.shulie.takin.cloud.open.constant.CloudApiConstant;
import io.shulie.takin.cloud.open.req.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.open.resp.common.CommonInfosResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.http.HttpHelper;
import io.shulie.takin.utils.http.TakinResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.tro.properties.TroCloudClientProperties;
import org.springframework.stereotype.Component;

/**
 * 公共信息接口Api实现
 *
 * @author lipeng
 * @date 2021-06-24 4:19 下午
 */
@Component
public class CommonInfoApiImpl extends CloudCommonApi implements CommonInfoApi {

    @Autowired
    private TroCloudClientProperties troCloudClientProperties;

    /**
     * 获取cloud配置信息
     *
     * @return
     */
    @Override
    public ResponseResult<CommonInfosResp> getCloudConfigurationInfos(CloudCommonInfoWrapperReq request) {
        TakinResponseEntity<ResponseResult<CommonInfosResp>> takinResponseEntity =
                HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.TROCLOUD_COMMON_INFOS_URI,
                    getHeaders(request), request, new TypeReference<ResponseResult<CommonInfosResp>>() {});
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
                takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }
}
