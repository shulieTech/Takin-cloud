package io.shulie.takin.cloud.biz.service.engine;

import io.shulie.takin.cloud.biz.input.engine.EnginePluginWrapperInput;
import io.shulie.takin.cloud.biz.output.engine.EnginePluginDetailOutput;
import io.shulie.takin.cloud.biz.output.engine.EnginePluginSimpleInfoOutput;
import io.shulie.takin.common.beans.response.ResponseResult;

import java.util.List;
import java.util.Map;

/**
 * 引擎接口
 *
 * @author lipeng
 * @date 2021-01-06 3:07 下午
 */
public interface EnginePluginService {

    /**
     * 查询引擎支持的插件信息
     *
     * @param pluginTypes 插件类型
     *
     * @return -
     */
    Map<String, List<EnginePluginSimpleInfoOutput>> findEngineAvailablePluginsByType(List<String> pluginTypes);

    /**
     * 根据插件ID获取插件详情信息
     *
     * @param pluginId
     * @return -
     */
    ResponseResult<EnginePluginDetailOutput> findEnginePluginDetails(Long pluginId);

    /**
     * 保存引擎插件
     *
     * @param input
     * @return -
     */
    void saveEnginePlugin(EnginePluginWrapperInput input);

    /**
     *
     *
     * @param pluginId
     * @param status
     * @return -
     */
    void changeEnginePluginStatus(Long pluginId, Integer status);

}
