package io.shulie.plugin.enginecall;


import com.alibaba.fastjson.JSONObject;
import com.pamirs.takin.entity.domain.vo.report.SceneTaskNotifyParam;
import io.shulie.plugin.enginecall.service.EngineCallService;
import io.shulie.takin.cloud.biz.service.engine.EngineConfigService;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskService;
import io.shulie.takin.cloud.common.constants.PressureInstanceRedisKey;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.utils.GsonUtil;
import io.shulie.takin.constants.TakinRequestConstant;
import io.shulie.takin.ext.api.EngineCallExtApi;
import io.shulie.takin.ext.content.enginecall.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zhaoyong
 */
@Slf4j
@Extension
public class EngineCallExtImpl implements EngineCallExtApi {

    @Value("${console.url}")
    private String console;

    @Value("${spring.redis.host}")
    private String engineRedisAddress;

    @Value("${spring.redis.port}")
    private String engineRedisPort;

    @Value("${spring.redis.sentinel.nodes:}")
    private String engineRedisSentinelNodes;

    @Value("${spring.redis.sentinel.master:}")
    private String engineRedisSentinelMaster;

    @Value("${spring.redis.password}")
    private String engineRedisPassword;

    @Value("${pradar.zk.servers}")
    private String zkServers;

    @Value("${engine.log.queue.size:25000}")
    private String logQueueSize;
    @Value("${pressure.engine.backendQueueCapacity:5000}")
    private String pressureEngineBackendQueueCapacity;
    /**
     * 调度任务路径
     */
    @Value("${pressure.engine.task.dir:./engine}")
    private String taskDir;

    @Value("${script.path}")
    private String scriptPath;
    /**
     * 调度任务设置
     */
    @Value("${pressure.engine.memSetting:-Xmx512m -Xms512m -Xss256K -XX:MaxMetaspaceSize=256m}")
    private String pressureEngineMemSetting;

    @Autowired
    private SceneTaskService sceneTaskService;
    @Autowired
    private EngineCallService engineCallService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private EngineConfigService engineConfigService;

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
        Map<String, Object> configMap = new HashMap<>();
        ScheduleStartRequestExt scheduleStartRequest = request.getRequest();
        configMap.put("name", ScheduleConstants.getConfigMapName(scheduleStartRequest.getSceneId(), scheduleStartRequest.getTaskId(),
                scheduleStartRequest.getTenantId()));
        JSONObject param = new JSONObject();
        param.put("scriptPath", scriptPath + SceneManageConstant.FILE_SPLIT + scheduleStartRequest.getScriptPath());
        param.put("pressureEnginePathUrl",scriptPath + SceneManageConstant.FILE_SPLIT);
        param.put("extJarPath", "");
        param.put("isLocal", true);
        param.put("taskDir", taskDir);
        param.put("pressureMode", scheduleStartRequest.getPressureMode());
        param.put("continuedTime", scheduleStartRequest.getContinuedTime());
        if (scheduleStartRequest.getExpectThroughput() != null) {
            param.put("expectThroughput", scheduleStartRequest.getExpectThroughput() / scheduleStartRequest.getTotalIp());
        }
        //将jar包放入引擎目录中，打包后会放入ext目录
        if (CollectionUtils.isNotEmpty(scheduleStartRequest.getDataFile())) {
            List<String> jarFilePaths = scheduleStartRequest.getDataFile().stream().filter(o -> o.getName().endsWith(".jar"))
                    .map(reqExt ->{
                        return scriptPath + SceneManageConstant.FILE_SPLIT + reqExt.getPath();
                    }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(jarFilePaths)) {
                jarFilePaths.forEach(scheduleStartRequest::addEnginePluginsFilePath);
            }
        }

        param.put("rampUp", scheduleStartRequest.getRampUp());
        param.put("steps", scheduleStartRequest.getSteps());
        // add start by lipeng 添加压测引擎插件文件夹目录 enginePluginFolderPath
        param.put("enginePluginsFilePath", scheduleStartRequest.getEnginePluginsFilePath());
        // add end
        JSONObject enginePressureParams = new JSONObject();
        enginePressureParams.put("pressureEngineBackendQueueCapacity", this.pressureEngineBackendQueueCapacity);
        enginePressureParams.put("engineRedisAddress", engineRedisAddress);
        enginePressureParams.put("engineRedisPort", engineRedisPort);
        enginePressureParams.put("engineRedisSentinelNodes", engineRedisSentinelNodes);
        enginePressureParams.put("engineRedisSentinelMaster", engineRedisSentinelMaster);
        enginePressureParams.put("engineRedisPassword", engineRedisPassword);
        BigDecimal podTpsNum = new BigDecimal(0);
        if (scheduleStartRequest.getTps() != null){
            podTpsNum = new BigDecimal(scheduleStartRequest.getTps()).divide(new BigDecimal(scheduleStartRequest.getTotalIp()), 0, BigDecimal.ROUND_UP);
        }
        if (scheduleStartRequest.isTryRun()) {
            //如果是巡检任务，则覆盖压测类型为巡检类型
            //enginePressureParams.put("enginePressureMode","4");
            enginePressureParams.put("fixed_timer", String.valueOf(scheduleStartRequest.getFixedTimer()));
            enginePressureParams.put("loops_num", String.valueOf(scheduleStartRequest.getLoopsNum()));
        }
        enginePressureParams.put("tpsTargetLevel", podTpsNum.longValue());
        enginePressureParams.put("enginePressureMode", scheduleStartRequest.getPressureType() == null ? "" : scheduleStartRequest.getPressureType().toString());
        enginePressureParams.put("traceSampling", StringUtils.isBlank(engineConfigService.getLogSimpling()) ? "1" : engineConfigService.getLogSimpling());
        enginePressureParams.put("ptlLogConfig",JSONObject.toJSONString(engineConfigService.getEnginePtlConfig()));
        enginePressureParams.put("zkServers",zkServers);
        enginePressureParams.put("logQueueSize",logQueueSize);
        if (scheduleStartRequest.getBusinessTpsData() != null) {
            List<Map<String, String>> businessActivities = new ArrayList<>();
            scheduleStartRequest.getBusinessTpsData().forEach((k, v) -> {
                Map<String, String> businessActivity = new HashMap<>();
                businessActivity.put("elementTestName", k);
                businessActivity.put("throughputPercent", new BigDecimal(v).multiply(new BigDecimal(100))
                        .divide(new BigDecimal(scheduleStartRequest.getTps()), 0, BigDecimal.ROUND_UP).toString());
                businessActivities.add(businessActivity);
            });
            enginePressureParams.put("businessActivities", businessActivities);
        }
        param.put("enginePressureParams", enginePressureParams);

        String engineInstanceRedisKey = PressureInstanceRedisKey.getEngineInstanceRedisKey(scheduleStartRequest.getSceneId(), scheduleStartRequest.getTaskId(),
                scheduleStartRequest.getTenantId());
        redisTemplate.opsForHash().put(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_ALL_LIMIT, scheduleStartRequest.getTps() + "");
        redisTemplate.opsForHash().put(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_LIMIT, podTpsNum + "");
        redisTemplate.opsForHash().put(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_POD_NUM, scheduleStartRequest.getTotalIp() + "");
        redisTemplate.expire(engineInstanceRedisKey, 10, TimeUnit.DAYS);
        param.put(TakinRequestConstant.CLUSTER_TEST_SCENE_HEADER_VALUE, scheduleStartRequest.getSceneId());
        param.put(TakinRequestConstant.CLUSTER_TEST_TASK_HEADER_VALUE, scheduleStartRequest.getTaskId());
        //  客户id
        param.put(TakinRequestConstant.CLUSTER_TEST_CUSTOMER_HEADER_VALUE, scheduleStartRequest.getTenantId());

        param.put("consoleUrl",
                console + ScheduleConstants.getConsoleUrl(request.getRequest().getSceneId(),
                        request.getRequest().getTaskId(),
                        request.getRequest().getTenantId()));
        param.put("takinCloudCallbackUrl", console + "/api/engine/callback");
        // 解决 单个pod ,但文件处于需要切割分类状态的bug
        param.put("podCount", scheduleStartRequest.getTotalIp());
        param.put("fileSets", scheduleStartRequest.getDataFile());
        param.put("businessMap", GsonUtil.gsonToString(scheduleStartRequest.getBusinessData()));
        param.put("memSetting", pressureEngineMemSetting);
        configMap.put("engine.conf", param.toJSONString());
        engineCallService.createConfigMap(configMap, PressureInstanceRedisKey.getEngineInstanceRedisKey(request.getRequest().getSceneId(),
                request.getRequest().getTaskId(), request.getRequest().getTenantId()));
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
