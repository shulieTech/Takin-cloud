package io.shulie.takin.cloud.sdk.impl.engine;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;

import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.entrypoint.engine.CloudEngineApi;
import io.shulie.takin.cloud.sdk.service.CloudApiSenderService;
import io.shulie.takin.cloud.sdk.model.response.engine.EnginePluginDetailResp;
import io.shulie.takin.cloud.sdk.model.response.engine.EnginePluginSimpleInfoResp;
import io.shulie.takin.cloud.sdk.model.request.engine.EnginePluginFetchWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.engine.EnginePluginDetailsWrapperReq;

/**
 * 引擎接口实现
 *
 * @author lipeng
 * @author 张天赐
 * @date 2021-01-20 3:33 下午
 */
@Service
public class CloudEngineApiImpl implements CloudEngineApi {

    @Resource
    CloudApiSenderService cloudApiSenderService;

    /**
     * 根据插件类型获取插件列表
     *
     * @param request 请求参数
     * @return -
     */
    @Override
    public Map<String, List<EnginePluginSimpleInfoResp>> listEnginePlugins(EnginePluginFetchWrapperReq request) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_ENGINE_PLUGIN, EntrypointUrl.METHOD_ENGINE_PLUGIN_LIST),
            request, new TypeReference<ResponseResult<Map<String, List<EnginePluginSimpleInfoResp>>>>() {}).getData();
    }

    /**
     * 根据插件ID获取插件详情
     *
     * @param request 请求参数
     * @return -
     */
    @Override
    public EnginePluginDetailResp getEnginePluginDetails(EnginePluginDetailsWrapperReq request) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_ENGINE_PLUGIN, EntrypointUrl.METHOD_ENGINE_PLUGIN_DETAILS),
            request, new TypeReference<ResponseResult<EnginePluginDetailResp>>() {}).getData();

    }

}
