package io.shulie.plugin.enginecall;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.pamirs.takin.entity.domain.vo.report.SceneTaskNotifyParam;
import io.shulie.plugin.enginecall.service.EngineCallService;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskService;
import io.shulie.takin.cloud.common.constants.PressureInstanceRedisKey;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.ext.api.EngineCallExtApi;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleRunRequest;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStartRequestExt;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStopRequestExt;
import io.shulie.takin.cloud.ext.content.enginecall.StrategyConfigExt;
import io.shulie.takin.cloud.ext.content.enginecall.StrategyOutputExt;
import io.shulie.takin.cloud.ext.content.enginecall.EngineRunConfig;
import io.shulie.takin.cloud.ext.helper.DataConvertHelper;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author zhaoyong
 */
@Slf4j
@Extension
public class EngineCallExtImpl implements EngineCallExtApi {

    /**
     * 调度任务路径
     */
    @Value("${pressure.engine.task.dir:./engine}")
    private String taskDir;

    @Value("${script.path}")
    private String scriptPath;

    @Resource
    private SceneTaskService sceneTaskService;
    @Resource
    private EngineCallService engineCallService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    @Override
    public String buildJob(ScheduleRunRequest request) {

        //创建容器需要的配置文件
        createEngineConfigMap(request);
        //通知配置文件建立成功
        notifyTaskResult(request);
        // 启动压测
        return engineCallService.createJob(request.getRequest().getSceneId(), request.getRequest().getTaskId(),
            request.getRequest().getTenantId());

    }

    @Override
    public void deleteJob(ScheduleStopRequestExt scheduleStopRequest) {
        engineCallService.deleteJob(scheduleStopRequest.getJobName(), scheduleStopRequest.getEngineInstanceRedisKey());
        engineCallService.deleteConfigMap(scheduleStopRequest.getEngineInstanceRedisKey());
    }

    @Override
    public List<String> getAllRunningJobName() {
        return engineCallService.getAllRunningJobName();

    }

    @Override
    public String getJobStatus(String jobName) {
        return engineCallService.getJobStatus(jobName);
    }

    @Override
    public StrategyOutputExt getPressureNodeNumRange(StrategyConfigExt strategyConfigExt) {
        StrategyOutputExt strategyOutputExt = new StrategyOutputExt();
        strategyOutputExt.setMin(1);
        strategyOutputExt.setMax(1);
        return strategyOutputExt;
    }

    @Override
    public StrategyConfigExt getDefaultStrategyConfig() {
        StrategyConfigExt strategyConfigExt = new StrategyConfigExt();
        strategyConfigExt.setStrategyName("开源默认策略");
        strategyConfigExt.setThreadNum(1000);
        strategyConfigExt.setTpsNum(2000);
        strategyConfigExt.setLimitCpuNum(new BigDecimal(2));
        strategyConfigExt.setLimitMemorySize(new BigDecimal(3076));
        strategyConfigExt.setCpuNum(new BigDecimal(2));
        strategyConfigExt.setMemorySize(new BigDecimal(3076));
        return strategyConfigExt;
    }

    /**
     * 创建引擎配置文件
     */
    public void createEngineConfigMap(ScheduleRunRequest request) {
        ScheduleStartRequestExt startRequest = request.getRequest();
        Long sceneId = startRequest.getSceneId();
        Long taskId = startRequest.getTaskId();
        Long customerId = startRequest.getTenantId();

        Map<String, Object> configMap = new HashMap<>(0);
        configMap.put("name", ScheduleConstants.getConfigMapName(sceneId, taskId, customerId));

        EngineRunConfig config = DataConvertHelper.buildEngineRunConfig(request, scriptPath);
        config.setIsLocal(true);
        config.setTaskDir(taskDir);

        String engineInstanceRedisKey = PressureInstanceRedisKey.getEngineInstanceRedisKey(sceneId, taskId, customerId);
        redisTemplate.opsForHash().put(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_ALL_LIMIT, startRequest.getTotalTps() + "");
        Long podTpsNum = null;
        if (null != startRequest.getTps()) {
            podTpsNum = Double.doubleToLongBits(startRequest.getTps());
        }
        redisTemplate.opsForHash().put(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_LIMIT, podTpsNum + "");
        redisTemplate.opsForHash().put(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_POD_NUM, startRequest.getTotalIp() + "");
        redisTemplate.expire(engineInstanceRedisKey, 10, TimeUnit.DAYS);
        configMap.put("engine.conf", JsonUtil.toJson(config));
        engineCallService.createConfigMap(configMap, engineInstanceRedisKey);
    }

    private void notifyTaskResult(ScheduleRunRequest request) {
        SceneTaskNotifyParam notify = new SceneTaskNotifyParam();
        notify.setSceneId(request.getRequest().getSceneId());
        notify.setTaskId(request.getRequest().getTaskId());
        notify.setTenantId(request.getRequest().getTenantId());
        notify.setStatus("started");
        sceneTaskService.taskResultNotify(notify);
    }

    @Override
    public String getType() {
        return "local_engine";
    }
}
