package io.shulie.takin.cloud.sdk.api.common;

import io.shulie.takin.cloud.sdk.resp.common.CommonInfosResp;
import io.shulie.takin.cloud.sdk.req.common.CloudCommonInfoWrapperReq;

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
