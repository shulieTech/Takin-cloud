package io.shulie.takin.cloud.biz.service.engine;

import io.shulie.takin.cloud.biz.output.engine.EngineLogPtlConfigOutput;

/**
 * @author moriarty
 */
public interface EngineConfigService {

    /**
     * 获取引擎日志配置
     * @return -
     */
    EngineLogPtlConfigOutput getEnginePtlConfig();

    /**
     * 获取日志采样率
     * @return -
     */
    String getLogSimpling();

    /**
     * 获取日志推送服务
     * @param failServer
     * @return -
     */
    String getLogPushServer(String failServer);

    /**
     * 获取需要挂载本地磁盘的场景ID
     *
     * @return -
     */
    String[] getLocalMountSceneIds();

}
