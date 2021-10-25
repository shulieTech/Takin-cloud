package io.shulie.takin.cloud.sdk.impl.common;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;

import org.springframework.stereotype.Component;

import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.entrypoint.common.CommonInfoApi;
import io.shulie.takin.cloud.sdk.service.CloudApiSenderService;
import io.shulie.takin.cloud.sdk.model.response.common.CommonInfosResp;
import io.shulie.takin.cloud.sdk.model.request.common.CloudCommonInfoWrapperReq;

/**
 * 公共信息接口Api实现
 *
 * @author lipeng
 * @date 2021-06-24 4:19 下午
 */
@Component
public class CommonInfoApiImpl implements CommonInfoApi {

    @Resource
    CloudApiSenderService cloudApiSenderService;

    /**
     * 获取cloud配置信息
     *
     * @return -
     */
    @Override
    public CommonInfosResp getCloudConfigurationInfos(CloudCommonInfoWrapperReq request) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_COMMON, EntrypointUrl.METHOD_COMMON_CONFIG),
            request, new TypeReference<ResponseResult<CommonInfosResp>>() {}).getData();
    }
}
