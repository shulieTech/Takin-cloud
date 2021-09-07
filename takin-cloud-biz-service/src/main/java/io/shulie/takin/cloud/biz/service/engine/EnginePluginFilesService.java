package io.shulie.takin.cloud.biz.service.engine;

import io.shulie.takin.cloud.biz.output.engine.EnginePluginFileOutput;
import io.shulie.takin.cloud.common.bean.file.FileManageInfo;
import io.shulie.takin.cloud.biz.output.scenemanage.SceneManageWrapperOutput;

import java.util.List;

/**
 * 引擎插件文件信息接口
 *
 * @author lipeng
 * @date 2021-01-13 5:26 下午
 */
public interface EnginePluginFilesService {

    /**
     * 根据插件id获取所有文件路径
     *
     * @param pluginIds
     */
    List<String> findPluginFilesPathByPluginIds(List<Long> pluginIds);

    /**
     * 根据插件id获取文件信息
     *
     * @param pluginId
     */
    List<EnginePluginFileOutput> findPluginFilesInfoByPluginId(Long pluginId);

    /**
     * 批量保存引擎插件文件信息
     *
     * @param pluginId 插件ID
     * @param files 文件信息
     */
    void batchSaveEnginePluginFiles(List<FileManageInfo> files, Long pluginId);

    /**
     * 根据插件Id和插件版本号获取文件路径
     * @param plugins
     * @return -
     */
    List<String> findPluginFilesPathByPluginIdAndVersion(List<SceneManageWrapperOutput.EnginePluginRefOutput> plugins);
}
