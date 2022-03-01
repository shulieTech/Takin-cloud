package io.shulie.takin.cloud.biz.service.common.impl;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.biz.output.common.CommonInfosOutput;
import io.shulie.takin.cloud.biz.service.common.CommonInfoService;
import io.shulie.takin.cloud.ext.content.enginecall.StrategyConfigExt;
import io.shulie.takin.cloud.biz.service.strategy.StrategyConfigService;

/**
 * 引擎插件文件信息接口
 *
 * @author lipeng
 * @date 2021-06-24 3:51 下午
 */
@Slf4j
@Service
public class CommonInfoServiceImpl implements CommonInfoService {
    @Resource
    private AppConfig appConfig;
    @Resource
    private StrategyConfigService strategyConfigService;

    /**
     * 获取公共配置信息
     *
     * @return -
     */
    @Override
    public CommonInfosOutput getCommonConfigurationInfos() {
        CommonInfosOutput result = new CommonInfosOutput();
        result.setPressureEngineVersion(getCurrentPressureEngineImage());
        result.setCloudVersion(appConfig.getCloudVersion());
        log.info("压测引擎版本：{},cloud版本:{}", result.getPressureEngineVersion(), result.getCloudVersion());
        return result;
    }

    /**
     * 当前压测引擎版本
     */
    private String getCurrentPressureEngineImage() {
        StrategyConfigExt config = strategyConfigService.getCurrentStrategyConfig();
        return CommonUtil.getValue(appConfig.getPressureEngineImage(), config, StrategyConfigExt::getPressureEngineImage);
    }
}
