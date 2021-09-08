package io.shulie.takin.cloud.biz.service.common.impl;

import io.shulie.takin.cloud.biz.output.common.CommonInfosOutput;
import io.shulie.takin.cloud.biz.service.common.CommonInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 引擎插件文件信息接口
 *
 * @author lipeng
 * @date 2021-06-24 3:51 下午
 */
@Slf4j
@Service
public class CommonInfoServiceImpl implements CommonInfoService {

    @Value("${pressure.engine.images}")
    private String pressureEngineImage;

    @Value("${info.app.version}")
    private String cloudVersion;

    /**
     * 获取公共配置信息
     *
     * @return -
     */
    @Override
    public CommonInfosOutput getCommonConfigurationInfos() {
        CommonInfosOutput result = new CommonInfosOutput();
        result.setPressureEngineVersion(this.pressureEngineImage);
        result.setCloudVersion(cloudVersion);
        log.info("压测引擎版本：{},cloud版本:{}",this.pressureEngineImage,this.cloudVersion);
        return result;
    }
}
