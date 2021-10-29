package io.shulie.plugin.enginecall;


import com.pamirs.takin.entity.domain.vo.report.SceneTaskNotifyParam;
import io.shulie.plugin.enginecall.contents.EnginePressureConfig;
import io.shulie.plugin.enginecall.contents.EngineRunConfig;
import io.shulie.plugin.enginecall.service.EngineCallService;
import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.biz.service.engine.EngineConfigService;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskService;
import io.shulie.takin.cloud.common.constants.PressureInstanceRedisKey;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.common.utils.NumberUtil;
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
import java.util.*;
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

    @Autowired
    private SceneTaskService sceneTaskService;
    @Autowired
    private EngineCallService engineCallService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private EngineConfigService engineConfigService;

    @Autowired
    private AppConfig appConfig;

    @Override
    public String buildJob(ScheduleRunRequest request) {

        //创建容器需要的配置文件
        createEngineConfigMap(request);
        //通知配置文件建立成功
        notifyTaskResult(request);
        // 启动压测
        return engineCallService.createJob(request.getRequest().getSceneId(), request.getRequest().getTaskId(),
                request.getRequest().getCustomerId());

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
        StrategyConfigExt strategyConfig = request.getStrategyConfig();
        Long sceneId = startRequest.getSceneId();
        Long taskId = startRequest.getTaskId();
        Long customerId = startRequest.getCustomerId();

        Map<String, Object> configMap = new HashMap<>();
        configMap.put("name", ScheduleConstants.getConfigMapName(sceneId, taskId, customerId));

        EngineRunConfig config = new EngineRunConfig();
        config.setSceneId(sceneId);
        config.setTaskId(taskId);
        config.setCustomerId(customerId);
        config.setConsoleUrl(console + ScheduleConstants.getConsoleUrl(sceneId, taskId, customerId));
        config.setCallbackUrl(console + "/api/engine/callback");
        config.setPodCount(startRequest.getTotalIp());
        config.setScriptPath(scriptPath + SceneManageConstant.FILE_SPLIT + startRequest.getScriptPath());
        config.setPressureEnginePathUrl(scriptPath + SceneManageConstant.FILE_SPLIT);
        config.setExtJarPath("");
        config.setIsLocal(true);
        config.setTaskDir(taskDir);
        config.setContinuedTime(startRequest.getContinuedTime());
        if (null != startRequest.getExpectThroughput()) {
            config.setExpectThroughput(startRequest.getExpectThroughput() / startRequest.getTotalIp());
        }
        if (CollectionUtils.isNotEmpty(startRequest.getDataFile())) {
            List<String> jarFiles = startRequest.getDataFile().stream().filter(Objects::nonNull)
                    .filter(o -> StringUtils.isNotBlank(o.getName()))
                    .filter(o -> o.getName().startsWith(".jar"))
                    .map(ScheduleStartRequestExt.DataFile::getPath)
                    .filter(StringUtils::isNotBlank)
                    .map(s -> scriptPath + SceneManageConstant.FILE_SPLIT + s)
                    .collect(Collectors.toList());
            config.setEnginePluginsFiles(jarFiles);
        }
        if (CollectionUtils.isNotEmpty(startRequest.getDataFile())) {
            startRequest.getDataFile().forEach(
                    dataFile -> dataFile.setPath(ScheduleConstants.ENGINE_SCRIPT_FILE_PATH + dataFile.getPath())
            );
            config.setFileSets(startRequest.getDataFile());
        }
        config.setBusinessMap(startRequest.getBusinessData());
        String memSetting = CommonUtil.getValue(appConfig.getK8sJvmSettings(), strategyConfig, StrategyConfigExt::getK8sJvmSettings);
        config.setMemSetting(memSetting);

        EnginePressureConfig pressureConfig = new EnginePressureConfig();
        pressureConfig.setPressureEngineBackendQueueCapacity(this.pressureEngineBackendQueueCapacity);
        pressureConfig.setEngineRedisAddress(this.engineRedisAddress);
        pressureConfig.setEngineRedisPort(this.engineRedisPort);
        pressureConfig.setEngineRedisSentinelNodes(this.engineRedisSentinelNodes);
        pressureConfig.setEngineRedisSentinelMaster(this.engineRedisSentinelMaster);
        pressureConfig.setEngineRedisPassword(this.engineRedisPassword);
        if (startRequest.isTryRun()) {
            pressureConfig.setFixedTimer(startRequest.getFixedTimer());
            pressureConfig.setLoopsNum(startRequest.getLoopsNum());
        }
        Integer traceSampling = 1;
        if (!startRequest.isTryRun() && !startRequest.isInspect() && null != engineConfigService.getLogSimpling()) {
            traceSampling = engineConfigService.getLogSimpling();
        }
        pressureConfig.setTraceSampling(traceSampling);
        pressureConfig.setPtlLogConfig(engineConfigService.getEnginePtlConfig());
        pressureConfig.setZkServers(zkServers);
        pressureConfig.setLogQueueSize(NumberUtil.parseInt(logQueueSize, 25000));
        pressureConfig.setThreadGroupConfigMap(startRequest.getThreadGroupConfigMap());

        Long podTpsNum = null;
        if (null != startRequest.getTps()){
            double tps = NumberUtil.getRate(startRequest.getTps(), startRequest.getTotalIp());
            pressureConfig.setTpsTargetLevel(tps);
            podTpsNum = Double.doubleToLongBits(tps);
        }
        //TODO 目标参数处理
        if (startRequest.getBusinessTpsData() != null) {
            List<Map<String, String>> businessActivities = new ArrayList<>();
            startRequest.getBusinessTpsData().forEach((k, v) -> {
                Map<String, String> businessActivity = new HashMap<>();
                businessActivity.put("elementTestName", k);
                businessActivity.put("throughputPercent", new BigDecimal(v).multiply(new BigDecimal(100))
                        .divide(new BigDecimal(startRequest.getTps()), 0, BigDecimal.ROUND_UP).toString());
                businessActivities.add(businessActivity);
            });
            pressureConfig.setBusinessActivities(businessActivities);
        }
        if (null != strategyConfig) {
            pressureConfig.setTpsThreadMode(strategyConfig.getTpsThreadMode());
            pressureConfig.setTpsTargetLevelFactor(strategyConfig.getTpsTargetLevelFactor());
            pressureConfig.setMaxThreadNum(strategyConfig.getTpsRealThreadNum());
        }
        config.setPressureConfig(pressureConfig);


//        JSONObject param = new JSONObject();
//        param.put("scriptPath", scriptPath + SceneManageConstant.FILE_SPLIT + startRequest.getScriptPath());
//        param.put("pressureEnginePathUrl",scriptPath + SceneManageConstant.FILE_SPLIT);
//        param.put("extJarPath", "");
//        param.put("isLocal", true);
//        param.put("taskDir", taskDir);
////        param.put("pressureMode", scheduleStartRequest.getPressureMode());
//        param.put("continuedTime", startRequest.getContinuedTime());
//        if (null != startRequest.getExpectThroughput()) {
//            param.put("expectThroughput", startRequest.getExpectThroughput() / startRequest.getTotalIp());
//        }
//        //将jar包放入引擎目录中，打包后会放入ext目录
//        if (CollectionUtils.isNotEmpty(startRequest.getDataFile())) {
//            List<String> jarFilePaths = startRequest.getDataFile().stream().filter(Objects::nonNull)
//                    .filter(o -> o.getName().endsWith(".jar"))
//                    .map(reqExt ->{
//                        return scriptPath + SceneManageConstant.FILE_SPLIT + reqExt.getPath();
//                    }).collect(Collectors.toList());
//            if (CollectionUtils.isNotEmpty(jarFilePaths)) {
//                jarFilePaths.forEach(startRequest::addEnginePluginsFilePath);
//            }
//        }
//
////        param.put("rampUp", scheduleStartRequest.getRampUp());
////        param.put("steps", scheduleStartRequest.getSteps());
//        // add start by lipeng 添加压测引擎插件文件夹目录 enginePluginFolderPath
//        param.put("enginePluginsFilePath", startRequest.getEnginePluginsFilePath());
        // add end
//        JSONObject enginePressureParams = new JSONObject();
//        enginePressureParams.put("podNum", startRequest.getTotalIp());
//        enginePressureParams.put("pressureEngineBackendQueueCapacity", this.pressureEngineBackendQueueCapacity);
//        enginePressureParams.put("engineRedisAddress", engineRedisAddress);
//        enginePressureParams.put("engineRedisPort", engineRedisPort);
//        enginePressureParams.put("engineRedisSentinelNodes", engineRedisSentinelNodes);
//        enginePressureParams.put("engineRedisSentinelMaster", engineRedisSentinelMaster);
//        enginePressureParams.put("engineRedisPassword", engineRedisPassword);

//        if (startRequest.isTryRun()) {
//            //如果是巡检任务，则覆盖压测类型为巡检类型
//            //enginePressureParams.put("enginePressureMode","4");
//            enginePressureParams.put("fixed_timer", String.valueOf(startRequest.getFixedTimer()));
//            enginePressureParams.put("loops_num", String.valueOf(startRequest.getLoopsNum()));
//        }
//        enginePressureParams.put("tpsTargetLevel", podTpsNum.longValue()+"");
//        enginePressureParams.put("enginePressureMode", scheduleStartRequest.getPressureType() == null ? "" : scheduleStartRequest.getPressureType().toString());
        //巡检和脚本调试采样率都为1
//        String traceSampling = "1";
//        if (startRequest.isTryRun() || startRequest.isInspect()) {
//            enginePressureParams.put("traceSampling", traceSampling);
//        } else {
//            traceSampling = CommonUtil.getValue(traceSampling, engineConfigService, EngineConfigService::getLogSimpling);
//            enginePressureParams.put("traceSampling", traceSampling);
//        }
//        if (scheduleStartRequest.getPressureType().equals(PressureTypeEnums.TRY_RUN.getCode())
//                || scheduleStartRequest.getPressureType().equals(PressureTypeEnums.INSPECTION_MODE.getCode())) {
//            enginePressureParams.put("traceSampling", traceSampling);
//        } else {
//            enginePressureParams.put("traceSampling",
//                    StringUtils.isBlank(engineConfigService.getLogSimpling()) ? traceSampling : engineConfigService.getLogSimpling());
//        }
//        enginePressureParams.put("ptlLogConfig", JsonUtil.toJson(engineConfigService.getEnginePtlConfig()));
//        enginePressureParams.put("zkServers", zkServers);
//        enginePressureParams.put("logQueueSize", logQueueSize);
//        enginePressureParams.put("threadGroupConfig", JsonUtil.toJson(startRequest.getThreadGroupConfig()));
//        BigDecimal podTpsNum = new BigDecimal(0);
//        if (startRequest.getTps() != null){
//            podTpsNum = new BigDecimal(startRequest.getTps()).divide(new BigDecimal(startRequest.getTotalIp()), 0, BigDecimal.ROUND_UP);
//        }
//        if (startRequest.getBusinessTpsData() != null) {
//            List<Map<String, String>> businessActivities = new ArrayList<>();
//            startRequest.getBusinessTpsData().forEach((k, v) -> {
//                Map<String, String> businessActivity = new HashMap<>();
//                businessActivity.put("elementTestName", k);
//                businessActivity.put("throughputPercent", new BigDecimal(v).multiply(new BigDecimal(100))
//                        .divide(new BigDecimal(startRequest.getTps()), 0, BigDecimal.ROUND_UP).toString());
//                businessActivities.add(businessActivity);
//            });
//            enginePressureParams.put("businessActivities", businessActivities);
//        }

//        if (null != strategyConfig) {
//            if (null != strategyConfig.getTpsThreadMode()) {
//                enginePressureParams.put("tpsThreadMode", String.valueOf(strategyConfig.getTpsThreadMode()));
//            }
//            enginePressureParams.put("tpsTargetLevelFactor", String.valueOf(strategyConfig.getTpsTargetLevelFactor()));
//            if (null != strategyConfig.getTpsRealThreadNum()) {
//                enginePressureParams.put("maxThreadNum", String.valueOf(strategyConfig.getTpsRealThreadNum()));
//            }
//        }
//        param.put("enginePressureParams", enginePressureParams);

        String engineInstanceRedisKey = PressureInstanceRedisKey.getEngineInstanceRedisKey(startRequest.getSceneId(), startRequest.getTaskId(),
                startRequest.getCustomerId());
        redisTemplate.opsForHash().put(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_ALL_LIMIT, startRequest.getTps() + "");
        redisTemplate.opsForHash().put(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_LIMIT, podTpsNum + "");
        redisTemplate.opsForHash().put(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_POD_NUM, startRequest.getTotalIp() + "");
        redisTemplate.expire(engineInstanceRedisKey, 10, TimeUnit.DAYS);
//        param.put(TakinRequestConstant.CLUSTER_TEST_SCENE_HEADER_VALUE, startRequest.getSceneId());
//        param.put(TakinRequestConstant.CLUSTER_TEST_TASK_HEADER_VALUE, startRequest.getTaskId());
//        //  客户id
//        param.put(TakinRequestConstant.CLUSTER_TEST_CUSTOMER_HEADER_VALUE, startRequest.getCustomerId());

//        param.put("consoleUrl",
//                console + ScheduleConstants.getConsoleUrl(request.getRequest().getSceneId(),
//                        request.getRequest().getTaskId(),
//                        request.getRequest().getCustomerId()));
//        param.put("takinCloudCallbackUrl", console + "/api/engine/callback");
        // 解决 单个pod ,但文件处于需要切割分类状态的bug
//        param.put("podCount", startRequest.getTotalIp());
        //拼接文件路径
//        if (CollectionUtils.isNotEmpty(startRequest.getDataFile())) {
//            startRequest.getDataFile().forEach(
//                    dataFile -> {
//                        dataFile.setPath(ScheduleConstants.ENGINE_SCRIPT_FILE_PATH + dataFile.getPath());
//                    }
//            );
//        }
//        param.put("fileSets", startRequest.getDataFile());
//        param.put("businessMap", GsonUtil.gsonToString(startRequest.getBusinessData()));
//        String memSetting = CommonUtil.getValue(appConfig.getK8sJvmSettings(), strategyConfig, StrategyConfigExt::getK8sJvmSettings);
//        param.put("memSetting", memSetting);
        configMap.put("engine.conf", JsonUtil.toJson(config));
        engineCallService.createConfigMap(configMap, PressureInstanceRedisKey.getEngineInstanceRedisKey(request.getRequest().getSceneId(),
                request.getRequest().getTaskId(), request.getRequest().getCustomerId()));
    }

    private void notifyTaskResult(ScheduleRunRequest request) {
        SceneTaskNotifyParam notify = new SceneTaskNotifyParam();
        notify.setSceneId(request.getRequest().getSceneId());
        notify.setTaskId(request.getRequest().getTaskId());
        notify.setCustomerId(request.getRequest().getCustomerId());
        notify.setStatus("started");
        sceneTaskService.taskResultNotify(notify);
    }

    @Override
    public String getType() {
        return "local_engine";
    }
}
