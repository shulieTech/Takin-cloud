package io.shulie.takin.cloud.biz.service.engine.impl;

import java.util.concurrent.TimeUnit;

import io.shulie.takin.cloud.biz.service.engine.EngineService;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.EnginePluginUtils;
import io.shulie.takin.cloud.ext.api.EngineCallExtApi;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStopRequestExt;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.ext.content.enginecall.EngineRunConfig;
import io.shulie.takin.ext.content.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author liyuanba
 * @date 2021/11/26 11:30 上午
 */
@Slf4j
@Service
public class EngineServiceImpl implements EngineService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private EnginePluginUtils pluginUtils;

    /**
     * 检测资源：当未限制资源时掺入0
     * @param podNum 要启动的pod数量
     * @param requestCpu 单pod申请的cpu大小，单位：m
     * @param requestMemory 单pod申请的内存大小，单位:M
     * @param limitCpu 单pod申请的CPU最大限制，单位:M
     * @param limitMemory 单pod申请的内存最大限制，单位:M
     * @return 返回检测状态：0成功，1cpu资源不足，2memory资源不足
     */
    @Override
    public int check(Integer podNum, Long requestCpu, Long requestMemory, Long limitCpu, Long limitMemory) {
        if (null == podNum) {
            podNum = 1;
        }
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        if (null == engineCallExtApi) {
            return 0;
        }
        return engineCallExtApi.check(podNum, requestCpu, requestMemory, limitCpu, limitMemory);
    }

    @Override
    public ResponseResult<?> start(EngineRunConfig config) {
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        if (null != engineCallExtApi) {
            return engineCallExtApi.startJob(config);
        }
        return ResponseResult.fail("500", "找不到启动压测的插件", "请联系管理员");
    }

    /**
     * 删除引擎job
     */
    @Override
    public boolean deleteJob(String jobName, String engineInstanceRedisKey) {
        log.info("通知deleteJob模块， 监听到完成事件.....");
        try {
            ScheduleStopRequestExt scheduleStopRequest = new ScheduleStopRequestExt();
            scheduleStopRequest.setJobName(jobName);
            scheduleStopRequest.setEngineInstanceRedisKey(engineInstanceRedisKey);

            EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
            if (null != engineCallExtApi) {
                engineCallExtApi.deleteJob(scheduleStopRequest);
            }

            redisTemplate.expire(engineInstanceRedisKey, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：任务停止失败失败 --> 【deleteJob】处理finished事件异常: {}",
                TakinCloudExceptionEnum.TASK_STOP_DELETE_TASK_ERROR, e);
            return false;
        }
        return true;
    }
}
