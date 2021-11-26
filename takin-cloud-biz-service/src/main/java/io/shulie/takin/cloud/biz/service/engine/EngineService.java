package io.shulie.takin.cloud.biz.service.engine;

/**
 * @author liyuanba
 * @date 2021/11/26 11:17 上午
 */
public interface EngineService {
    /**
     * 删除引擎jog
     *
     * @param jobName                job名称
     * @param engineInstanceRedisKey 引擎实例Redis键
     */
    boolean deleteJob(String jobName, String engineInstanceRedisKey);
}
