package io.shulie.plugin.enginecall.service;

import java.util.List;
import java.util.Map;

/**
 * @author zhaoyong
 * 引擎调用接口
 */
public interface EngineCallService {

    /**
     * 创建任务
     *
     * @param sceneId  场景主键
     * @param taskId   任务主键
     * @param tenantId 租户主键
     * @return -
     */
    String createJob(Long sceneId, Long taskId, Long tenantId);

    /**
     * 删除任务
     *
     * @param jobName        任务名称
     * @param engineRedisKey 引擎--key
     */
    void deleteJob(String jobName, String engineRedisKey);

    /**
     * 创建引擎配置文件
     *
     * @param configMap      配置
     * @param engineRedisKey 引擎--key
     */
    void createConfigMap(Map<String, Object> configMap, String engineRedisKey);

    /**
     * 删除configMap
     *
     * @param engineRedisKey 引擎--key
     */
    void deleteConfigMap(String engineRedisKey);

    /**
     * 获取所有运行中的job名称
     *
     * @return -
     */
    List<String> getAllRunningJobName();

    /**
     * 获取job状态
     *
     * @param jobName 任务名称
     * @return -
     */
    String getJobStatus(String jobName);
}
