package io.shulie.takin.cloud.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.cloud.data.model.mysql.EnginePluginEntity;
import io.shulie.takin.cloud.data.result.engine.EnginePluginSimpleInfoResult;

import java.util.List;
import java.util.Map;

/**
 * 引擎Mapper
 *
 * @author lipeng
 * @date 2021-01-06 3:17 下午
 */
public interface EnginePluginMapper extends BaseMapper<EnginePluginEntity> {

    /**
     * 获取可用的插件列表
     *
     * @param pluginTypes 插件类型
     *
     * @return
     */
    List<EnginePluginSimpleInfoResult> selectAvailablePluginsByType(List<String> pluginTypes);

    /**
     * 根据插件id获取插件支持的版本信息
     *
     * @param pluginId
     * @return
     */
    List<Map> selectEnginePluginSupportedVersions(Long pluginId);

}
