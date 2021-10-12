package io.shulie.takin.cloud.open.api.impl.common;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import io.shulie.takin.cloud.open.api.common.CommonInfoApi;
import io.shulie.takin.cloud.open.constant.CloudApiConstant;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.open.resp.common.CommonInfosResp;
import io.shulie.takin.cloud.open.req.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.open.api.impl.sender.CloudApiSenderService;

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
        return cloudApiSenderService.get(CloudApiConstant.TROCLOUD_COMMON_INFOS_URI, request,
                new com.alibaba.fastjson.TypeReference<ResponseResult<CommonInfosResp>>() {})
            .getData();
    }
}
