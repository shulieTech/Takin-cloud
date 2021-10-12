package io.shulie.takin.cloud.open.api.impl.engine;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;

import org.springframework.stereotype.Component;

import io.shulie.takin.cloud.open.constant.CloudApiConstant;
import io.shulie.takin.cloud.open.api.engine.CloudEngineApi;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.open.resp.engine.EnginePluginDetailResp;
import io.shulie.takin.cloud.open.api.impl.sender.CloudApiSenderService;
import io.shulie.takin.cloud.open.resp.engine.EnginePluginSimpleInfoResp;
import io.shulie.takin.cloud.open.req.engine.EnginePluginFetchWrapperReq;
import io.shulie.takin.cloud.open.req.engine.EnginePluginDetailsWrapperReq;

/**
 * 引擎接口实现
 *
 * @author lipeng
 * @author 张天赐
 * @date 2021-01-20 3:33 下午
 */
@Component
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
        return cloudApiSenderService.post(CloudApiConstant.ENGINE_FETCH_PLUGINS_URI, request,
                new TypeReference<ResponseResult<Map<String, List<EnginePluginSimpleInfoResp>>>>() {})
            .getData();
    }

    /**
     * 根据插件ID获取插件详情
     *
     * @param request 请求参数
     * @return -
     */
    @Override
    public EnginePluginDetailResp getEnginePluginDetails(EnginePluginDetailsWrapperReq request) {
        return cloudApiSenderService.post(CloudApiConstant.ENGINE_FETCH_PLUGIN_DETAILS_URI, request,
                new TypeReference<ResponseResult<EnginePluginDetailResp>>() {})
            .getData();

    }

}
