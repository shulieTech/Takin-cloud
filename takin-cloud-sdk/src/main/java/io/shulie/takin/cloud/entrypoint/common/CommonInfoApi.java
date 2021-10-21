package io.shulie.takin.cloud.entrypoint.common;

import io.shulie.takin.cloud.sdk.model.response.common.CommonInfosResp;
import io.shulie.takin.cloud.sdk.model.request.common.CloudCommonInfoWrapperReq;

/**
 * 公共信息接口Api
 *
 * @author lipeng
 * @date 2021-06-24 4:07 下午
 */
public interface CommonInfoApi {

    /**
     * 获取cloud配置信息
     *
     * @param request 入参
     * @return -
     */
    CommonInfosResp getCloudConfigurationInfos(CloudCommonInfoWrapperReq request);
}
