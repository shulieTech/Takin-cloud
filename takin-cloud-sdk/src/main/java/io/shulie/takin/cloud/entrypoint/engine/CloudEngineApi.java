package io.shulie.takin.cloud.entrypoint.engine;

import java.util.Map;
import java.util.List;

import io.shulie.takin.cloud.sdk.model.response.engine.EnginePluginDetailResp;
import io.shulie.takin.cloud.sdk.model.response.engine.EnginePluginSimpleInfoResp;
import io.shulie.takin.cloud.sdk.model.request.engine.EnginePluginFetchWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.engine.EnginePluginDetailsWrapperReq;

/**
 * 压测引擎Api
 *
 * @author lipeng
 * @date 2021-01-18 5:13 下午
 */
public interface CloudEngineApi {

    /**
     * 根据插件类型获取插件列表
     *
     * @param request -
     * @return -
     */
    Map<String, List<EnginePluginSimpleInfoResp>> listEnginePlugins(EnginePluginFetchWrapperReq request);

    /**
     * 根据插件ID获取插件详情
     *
     * @param request -
     * @return -
     */
    EnginePluginDetailResp getEnginePluginDetails(EnginePluginDetailsWrapperReq request);
}