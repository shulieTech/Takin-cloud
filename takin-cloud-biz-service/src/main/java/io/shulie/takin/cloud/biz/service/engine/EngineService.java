package io.shulie.takin.cloud.biz.service.engine;

import io.shulie.takin.cloud.ext.content.enginecall.EngineRunConfig;
import io.shulie.takin.common.beans.response.ResponseResult;

/**
 * @author liyuanba
 * @date 2021/11/26 11:17 上午
 */
public interface EngineService {
    /**
     * 检测资源：当未限制资源时掺入0
     * @param podNum 要启动的pod数量
     * @param requestCpu 单pod申请的cpu大小，单位：m
     * @param requestMemory 单pod申请的内存大小，单位:M
     * @param limitCpu 单pod申请的CPU最大限制，单位:M
     * @param limitMemory 单pod申请的内存最大限制，单位:M
     * @return 返回检测状态：0成功，1cpu资源不足，2memory资源不足
     */
    int check(Integer podNum, Long requestCpu, Long requestMemory, Long limitCpu, Long limitMemory);

    /**
     * 启动压测
     * @param config 启动压测参数对象
     * @return  是否启动成功
     */
    ResponseResult<?> start(EngineRunConfig config);
    /**
     * 删除引擎jog
     *
     * @param jobName                job名称
     * @param engineInstanceRedisKey 引擎实例Redis键
     */
    boolean deleteJob(String jobName, String engineInstanceRedisKey);

}
