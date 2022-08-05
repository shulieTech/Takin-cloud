package io.shulie.takin.cloud.app.service;

import java.util.List;

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
     * @param scriptPath      脚本路径
     * @param dataFilePath    数据文件路径
     * @param attachmentsPath 附件路径
     * @return 任务主键
     */
    Long announce(String scriptPath, List<String> dataFilePath, List<String> attachmentsPath);

    /**
     * 报告结果
     *
     * @param id        任务主键
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
}
