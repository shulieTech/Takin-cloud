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
     * @param sceneId
     * @param taskId
     * @return -
     */
    String createJob(Long sceneId,Long taskId,Long customerId);

    /**
     * 删除任务
     * @param jobName
     * @param engineRedisKey
     */
    void deleteJob(String jobName,String engineRedisKey);

    /**
     * 创建引擎配置文件
     * @param configMap
     * @param engineRedisKey
     */
    void createConfigMap(Map<String, Object> configMap, String engineRedisKey);

    /**
     * 删除configMap
     * @param engineRedisKey
     */
    void deleteConfigMap(String engineRedisKey);

    /**
     * 获取所有运行中的job名称
     * @return -
     */
    List<String> getAllRunningJobName();

    /**
     * 获取job状态
     * @param jobName
     * @return -
     */
    String getJobStatus(String jobName);
}
