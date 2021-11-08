package io.shulie.takin.ext.helper;

import io.shulie.takin.ext.content.enginecall.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: liyuanba
 * @Date: 2021/11/3 9:37 上午
 */
public class DataConvertHelper {
    /**
     * 构建压测引擎的配置参数
     * @param request               启动参数
     * @param scriptFileDir         当前文档目录
     * @return 返回压测引擎的配置参数
     */
    public static EngineRunConfig buildEngineRunConfig(final ScheduleRunRequest request, final String scriptFileDir) {
        ScheduleStartRequestExt startRequest = request.getRequest();
        StrategyConfigExt strategyConfig = request.getStrategyConfig();
        Long sceneId = startRequest.getSceneId();
        Long taskId = startRequest.getTaskId();
        Long customerId = startRequest.getCustomerId();

//        Map<String, Object> configMap = new HashMap<>();
//        configMap.put("name", ScheduleConstants.getConfigMapName(sceneId, taskId, customerId));

        EngineRunConfig config = new EngineRunConfig();
        config.setSceneId(sceneId);
        config.setTaskId(taskId);
        config.setCustomerId(customerId);
        config.setConsoleUrl(startRequest.getConsole());
        config.setCallbackUrl(startRequest.getCallbackUrl());
        config.setPodCount(startRequest.getTotalIp());
        String scriptFile = CommonHelper.mergeDirPath(scriptFileDir, startRequest.getScriptPath());
        config.setScriptFile(scriptFile);
        config.setScriptFileDir(CommonHelper.mergeDirPath(scriptFileDir, File.separator));
//        config.setIsLocal(true);
//        config.setTaskDir(taskDir);
        config.setPressureScene(startRequest.getPressureScene());
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
                    .map(s -> CommonHelper.mergeDirPath(scriptFileDir, s))
//                    .map(s -> scriptPath + SceneManageConstant.FILE_SPLIT + s)
                    .collect(Collectors.toList());
            config.setEnginePluginsFiles(jarFiles);
            startRequest.getDataFile().forEach(
                    dataFile -> dataFile.setPath(CommonHelper.mergeDirPath(scriptFileDir, dataFile.getPath()))
            );
            config.setFileSets(startRequest.getDataFile());
        }
        config.setBusinessMap(startRequest.getBusinessData());
        config.setBindByXpathMd5(startRequest.getBindByXpathMd5());
        config.setMemSetting(request.getMemSetting());

        EnginePressureConfig pressureConfig = new EnginePressureConfig();
        pressureConfig.setPressureEngineBackendQueueCapacity(request.getPressureEngineBackendQueueCapacity());
        pressureConfig.setEngineRedisAddress(request.getEngineRedisAddress());
        pressureConfig.setEngineRedisPort(request.getEngineRedisPort());
        pressureConfig.setEngineRedisSentinelNodes(request.getEngineRedisSentinelNodes());
        pressureConfig.setEngineRedisSentinelMaster(request.getEngineRedisSentinelMaster());
        pressureConfig.setEngineRedisPassword(request.getEngineRedisPassword());
        if (startRequest.isTryRun()) {
            pressureConfig.setFixedTimer(startRequest.getFixedTimer());
            pressureConfig.setLoopsNum(startRequest.getLoopsNum());
        }
        pressureConfig.setTraceSampling(request.getTraceSampling());
        pressureConfig.setPtlLogConfig(request.getPtlLogConfig());
        pressureConfig.setZkServers(request.getZkServers());
        pressureConfig.setLogQueueSize(request.getLogQueueSize());
        pressureConfig.setThreadGroupConfigMap(startRequest.getThreadGroupConfigMap());

        pressureConfig.setTotalTpsTargetLevel(startRequest.getTotalTps());
        pressureConfig.setTpsTargetLevel(startRequest.getTps());

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

//        String engineInstanceRedisKey = PressureInstanceRedisKey.getEngineInstanceRedisKey(sceneId, taskId, customerId);
//        redisTemplate.opsForHash().put(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_ALL_LIMIT, startRequest.getTps() + "");
//        redisTemplate.opsForHash().put(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_LIMIT, podTpsNum + "");
//        redisTemplate.opsForHash().put(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.REDIS_TPS_POD_NUM, startRequest.getTotalIp() + "");
//        redisTemplate.expire(engineInstanceRedisKey, 10, TimeUnit.DAYS);
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
        return config;
//        configMap.put("engine.conf", JsonUtil.toJson(config));
//        engineCallService.createConfigMap(configMap, engineInstanceRedisKey);
    }
}
