package io.shulie.takin.cloud.open.api.impl.engine;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import io.shulie.takin.cloud.open.api.engine.CloudEngineApi;
import io.shulie.takin.cloud.open.api.impl.CloudCommonApi;
import io.shulie.takin.cloud.open.constant.CloudApiConstant;
import io.shulie.takin.cloud.open.req.engine.EnginePluginDetailsWrapperReq;
import io.shulie.takin.cloud.open.req.engine.EnginePluginFetchWrapperReq;
import io.shulie.takin.cloud.open.resp.engine.EnginePluginDetailResp;
import io.shulie.takin.cloud.open.resp.engine.EnginePluginSimpleInfoResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.http.HttpHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.tro.properties.TroCloudClientProperties;
import org.springframework.stereotype.Component;

/**
 * 引擎接口实现
 *
 * @author lipeng
 * @date 2021-01-20 3:33 下午
 */
@Component
public class CloudEngineApiImpl extends CloudCommonApi implements CloudEngineApi {

    @Autowired
    private TroCloudClientProperties troCloudClientProperties;

    /**
     * 根据插件类型获取插件列表
     *
     * @param request
     * @return
     */
    @Override
    public ResponseResult<Map<String, List<EnginePluginSimpleInfoResp>>> listEnginePlugins(
        EnginePluginFetchWrapperReq request) {
        return HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.ENGINE_FETCH_PLUGINS_URI,
            getHeaders(request),
            new TypeReference<ResponseResult<Map<String, List<EnginePluginSimpleInfoResp>>>>() {}, request).getBody();
    }

    /**
     * 根据插件ID获取插件详情
     *
     * @param request
     * @return
     */
    @Override
    public ResponseResult<EnginePluginDetailResp> getEnginePluginDetails(EnginePluginDetailsWrapperReq request) {
        return HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.ENGINE_FETCH_PLUGIN_DETAILS_URI,
            getHeaders(request), new TypeReference<ResponseResult<EnginePluginDetailResp>>() {}, request)
            .getBody();
    }

}
