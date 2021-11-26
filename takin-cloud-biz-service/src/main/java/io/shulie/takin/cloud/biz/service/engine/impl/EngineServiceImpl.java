package io.shulie.takin.cloud.biz.service.engine.impl;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import io.shulie.takin.cloud.ext.api.EngineCallExtApi;
import io.shulie.takin.cloud.common.utils.EnginePluginUtils;
import io.shulie.takin.cloud.biz.service.engine.EngineService;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStopRequestExt;

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
