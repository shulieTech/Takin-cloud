package io.shulie.takin.cloud.app.service;

import java.util.List;

import io.shulie.takin.cloud.data.entity.ScriptEntity;
import io.shulie.takin.cloud.model.request.job.script.BuildRequest;

/**
 * 脚本服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface ScriptService {
    /**
     * 下发任务
     *
     * @param watchmanId         调度器主键
     * @param callbackUrl        回调路径
     * @param attach             附加数据
     * @param scriptFilePath     脚本文件路径
     * @param dataFilePath       数据文件路径
     * @param attachmentFilePath 附件文件路径
     * @param pluginPath         插件路径
     * @return 脚本校验任务主键
     */
    Long announce(Long watchmanId, String callbackUrl, String attach,
        String scriptFilePath, List<String> dataFilePath,
        List<String> attachmentFilePath, List<String> pluginPath);

    /**
     * 报告结果
     *
     * @param id        施压任务主键
     * @param completed 是否完成
     * @param message   执行结果
     */
    void report(Long id, Boolean completed, String message);

    /**
     * 构建脚本
     *
     * @param scriptRequest 请求信息
     * @return 脚本内容
     */
    String build(BuildRequest scriptRequest);

    /**
     * 获取数据实体
     *
     * @param id 数据主键
     * @return 数据实体
     */
    ScriptEntity entity(Long id);
}
