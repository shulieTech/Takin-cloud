package io.shulie.takin.cloud.biz.service.common;

import io.shulie.takin.cloud.biz.output.common.CommonInfosOutput;

/**
 * 公共信息接口
 *
 * @author lipeng
 * @date 2021-06-24 3:49 下午
 */
public interface CommonInfoService {

    /**
     * 获取公共配置信息
     *
     * @return -
     */
    CommonInfosOutput getCommonConfigurationInfos();
}
