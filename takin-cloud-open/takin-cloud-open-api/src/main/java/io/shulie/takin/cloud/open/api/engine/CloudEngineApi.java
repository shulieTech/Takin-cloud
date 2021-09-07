package io.shulie.takin.cloud.open.api.engine;

import io.shulie.takin.cloud.open.req.engine.EnginePluginDetailsWrapperReq;
import io.shulie.takin.cloud.open.req.engine.EnginePluginFetchWrapperReq;
import io.shulie.takin.cloud.open.resp.engine.EnginePluginDetailResp;
import io.shulie.takin.cloud.open.resp.engine.EnginePluginSimpleInfoResp;
import io.shulie.takin.common.beans.response.ResponseResult;

import java.util.List;
import java.util.Map;

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
     * @param request
     * @return -
     */
    ResponseResult<Map<String, List<EnginePluginSimpleInfoResp>>> listEnginePlugins(EnginePluginFetchWrapperReq request);

    /**
     * 根据插件ID获取插件详情
     *
     * @param request
     * @return -
     */
    ResponseResult<EnginePluginDetailResp> getEnginePluginDetails(EnginePluginDetailsWrapperReq request);
}