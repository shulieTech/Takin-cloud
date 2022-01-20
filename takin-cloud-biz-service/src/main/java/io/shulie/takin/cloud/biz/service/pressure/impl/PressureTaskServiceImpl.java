package io.shulie.takin.cloud.biz.service.pressure.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.shulie.takin.cloud.biz.cloudserver.SceneManageDTOConvert;
import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.biz.convertor.PressureTaskConvertor;
import io.shulie.takin.cloud.biz.output.engine.EngineLogPtlConfigOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.pojo.PressureTaskPo;
import io.shulie.takin.cloud.biz.service.engine.EngineConfigService;
import io.shulie.takin.cloud.biz.service.engine.EnginePluginFilesService;
import io.shulie.takin.cloud.biz.service.pressure.PressureTaskService;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.biz.utils.DataUtils;
import io.shulie.takin.cloud.common.bean.TimeBean;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOpitons;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.TimeUnitEnum;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.common.utils.Md5Util;
import io.shulie.takin.cloud.common.utils.NumberUtil;
import io.shulie.takin.cloud.data.dao.pressure.PressureTaskDao;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;
import io.shulie.takin.cloud.data.param.pressure.PressureTaskQueryParam;
import io.shulie.takin.cloud.ext.content.enginecall.*;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.sdk.model.request.pressure.StartEngineReq;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneBusinessActivityRefOpen;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneScriptRefOpen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: liyuanba
 * @Date: 2021/12/29 1:55 下午
 */
@Slf4j
@Service
public class PressureTaskServiceImpl implements PressureTaskService {
    @Autowired
    private PressureTaskDao pressureTaskDao;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private EnginePluginFilesService enginePluginFilesService;
    @Autowired
    private EngineConfigService engineConfigService;
    @Autowired
    private SceneManageService sceneManageService;

    @Override
    public EngineRunConfig buildEngineRunConfig(PressureTaskPo po, StrategyConfigExt strategyConfig) {
        Long sceneId = po.getSceneId();
        Long taskId = po.getId();
        Long tenantId = po.getTenantId();
        EngineRunConfig config = new EngineRunConfig();
        config.setSceneId(sceneId);
        config.setTaskId(taskId);
        config.setCustomerId(tenantId);
        config.setPressureScene(po.getSceneType().getCode());
        String consoleUrl = DataUtils.mergeUrl(appConfig.getConsole(), ScheduleConstants.getConsoleUrl(sceneId, taskId, tenantId));
        config.setConsoleUrl(consoleUrl);
        String callbackUrl = DataUtils.mergeUrl(appConfig.getConsole(), "/api/engine/callback");
        config.setCallbackUrl(callbackUrl);
        config.setPodCount(po.getPodNum());
        config.setScriptFile(po.getScriptFile());
        config.setScriptFileDir(DataUtils.mergeDirPath(appConfig.getNfsDir(), File.separator));
        if (null != po.getHoldTime()) {
            TimeBean timeBean = new TimeBean(po.getHoldTime(), po.getHoldTimeUnit());
            long holdTime = timeBean.getSecondTime();
            config.setContinuedTime(holdTime);
        }

        if (null != po.getThroughput()) {
            config.setExpectThroughput(po.getThroughput() / po.getPodNum());
        }
        List<String> jarFiles = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(po.getUploadFiles())) {
            List<ScheduleStartRequestExt.DataFile> dataFiles = po.getUploadFiles().stream().filter(Objects::nonNull)
                    .map(f -> {
                        ScheduleStartRequestExt.DataFile dataFile = new ScheduleStartRequestExt.DataFile();
                        dataFile.setName(f.getFileName());
                        dataFile.setPath(f.getUploadPath());
                        dataFile.setSplit(f.getIsSplit() != null && f.getIsSplit() == 1);
                        dataFile.setOrdered(f.getIsOrderSplit() != null && f.getIsOrderSplit() == 1);
                        dataFile.setRefId(f.getId());
                        dataFile.setFileType(f.getFileType());
                        dataFile.setBigFile(f.getIsBigFile() != null && f.getIsBigFile() == 1);
                        dataFile.setFileMd5(Md5Util.md5File(f.getUploadPath()));
                        return dataFile;
                    })
                    .collect(Collectors.toList());
            config.setFileSets(dataFiles);

            po.getUploadFiles().stream().filter(Objects::nonNull)
                    .filter(file -> StringUtils.isNotBlank(file.getUploadPath()))
                    .filter(file -> file.getUploadPath().endsWith(".jar"))
                    .map(SceneScriptRefOpen::getUploadPath)
                    .filter(StringUtils::isNotBlank)
                    .forEach(jarFiles::add);
        }
        if (CollectionUtils.isNotEmpty(po.getEnginePlugins())) {
            po.getEnginePlugins().stream().filter(Objects::nonNull)
                    .map(p -> enginePluginFilesService.findPluginFilesPathByPluginIdAndVersion(p.getPluginId(), p.getVersion()))
                    .filter(StringUtils::isNotBlank)
                    .map(s -> DataUtils.mergeDirPath(appConfig.getScriptPath(), s))
                    .forEach(jarFiles::add);
        }
        config.setEnginePluginsFiles(jarFiles);

        Map<String, BusinessActivityExt> businessData = Maps.newHashMap();
        if (null != po.getBusinessActivityConfig()) {
            Integer tps = CommonUtil.sum(po.getBusinessActivityConfig(), SceneBusinessActivityRefOpen::getTargetRT);
            List<BusinessActivityExt> activities = CommonUtil.getList(po.getBusinessActivityConfig(), SceneManageDTOConvert.INSTANCE::of);
            if (CollectionUtils.isNotEmpty(activities)) {
                for (BusinessActivityExt d : activities) {
                    if (null != d.getTps()) {
                        d.setRate(NumberUtil.getRate(d.getTps(), tps));
                    }
                    businessData.put(d.getBindRef(), d);
                }
            }
            config.setBusinessMap(businessData);
        }
        config.setBindByXpathMd5(CollectionUtils.isNotEmpty(po.getScriptNodes()));
        config.setMemSetting(po.getJvmSettings());

        EnginePressureConfig pressureConfig = new EnginePressureConfig();
        pressureConfig.setPressureEngineBackendQueueCapacity(appConfig.getPressureEngineBackendQueueCapacity());
        pressureConfig.setEngineRedisAddress(appConfig.getEngineRedisAddress());
        pressureConfig.setEngineRedisPort(appConfig.getEngineRedisPort());
        pressureConfig.setEngineRedisSentinelNodes(appConfig.getEngineRedisSentinelNodes());
        pressureConfig.setEngineRedisSentinelMaster(appConfig.getEngineRedisSentinelMaster());
        pressureConfig.setEngineRedisPassword(appConfig.getEngineRedisPassword());
        if (po.getSceneType() == PressureSceneEnum.DEFAULT) {
            SceneManageQueryOpitons options = new SceneManageQueryOpitons();
            options.setIncludeBusinessActivity(true);
            options.setIncludeScript(true);
            SceneManageWrapperOutput scene = sceneManageService.getSceneManage(po.getSceneId(), options);
            if (null != scene) {
                pressureConfig.setThreadGroupConfigMap(scene.getThreadGroupConfigMap());
            }
        }

        pressureConfig.setTraceSampling(po.getTraceSampling());
        if (null != po.getLoopsNum()) {
            pressureConfig.setLoopsNum(po.getLoopsNum().intValue());
        }
        pressureConfig.setFixedTimer(po.getFixTimer());

        EngineLogPtlConfigOutput engineLogPtlConfigOutput = engineConfigService.getEnginePtlConfig();
        if (null != engineLogPtlConfigOutput) {
            PtlLogConfigExt ptlLogConfig = new PtlLogConfigExt();
            BeanUtils.copyProperties(engineLogPtlConfigOutput, ptlLogConfig);
            pressureConfig.setPtlLogConfig(ptlLogConfig);
        }

        pressureConfig.setZkServers(appConfig.getZkServers());
        pressureConfig.setLogQueueSize(NumberUtil.parseInt(appConfig.getLogQueueSize(), 25000));

//        pressureConfig.setTotalTpsTargetLevel(startRequest.getTotalTps());
//        pressureConfig.setTpsTargetLevel(startRequest.getTps());

        //获取策略
        int psThreadMode = CommonUtil.getValue(0, strategyConfig, StrategyConfigExt::getTpsThreadMode);
        double factor = CommonUtil.getValue(0.1d, strategyConfig, StrategyConfigExt::getTpsTargetLevelFactor);
        Integer maxThreadNum = CommonUtil.getValue(null, strategyConfig, StrategyConfigExt::getTpsRealThreadNum);
        pressureConfig.setTpsThreadMode(psThreadMode);
        pressureConfig.setTpsTargetLevelFactor(factor);
        pressureConfig.setMaxThreadNum(maxThreadNum);

        config.setPressureConfig(pressureConfig);
        return config;
    }

    @Override
    public PressureTaskPo buildPressureTask(StartEngineReq req) {
        PressureTaskPo po = new PressureTaskPo();
        po.setTenantId(req.getTenantId());
        po.setEnvCode(req.getEnvCode());
        po.setAdminId(req.getUserId());
        po.setSceneId(req.getId());
        po.setSceneType(PressureSceneEnum.value(req.getSceneType()));
        po.setHoldTime(req.getHoldTime());
        po.setHoldTimeUnit(TimeUnitEnum.value(req.getHoldTimeUnit()));
        po.setBusinessActivityConfig(req.getBusinessActivityConfig());
        po.setUploadFiles(req.getUploadFiles());
        po.setEnginePlugins(req.getEnginePlugins());
        po.setScriptId(req.getScriptId());
        po.setScriptDeployId(req.getScriptDeployId());
        po.setScriptNodes(JsonUtil.parseArray(req.getScriptNodes(), ScriptNode.class));
        po.setStatus(0);
        po.setPodNum(req.getPodNum());
        po.setThroughput(req.getThroughput());
        po.setFixTimer(req.getFixTimer());
        po.setLoopsNum(req.getLoopsNum());
        po.setContinueRead(req.getContinueRead());
        String scriptFile = req.getUploadFiles().stream().filter(Objects::nonNull)
                .filter(f -> 0 == f.getFileType())
                .map(SceneScriptRefOpen::getUploadPath)
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElse("");
        po.setScriptFile(scriptFile);
        List<SceneScriptRefOpen> files = req.getUploadFiles().stream().filter(Objects::nonNull)
                .filter(f -> !f.getUploadPath().equals(scriptFile))
                .collect(Collectors.toList());
        po.setUploadFiles(files);

        //取样率
        int traceSampling = 1;
        switch (po.getSceneType()) {
            case TRY_RUN:
            case INSPECTION_MODE:
                break;
            case FLOW_DEBUG:
                po.setLoopsNum(1000L);
                traceSampling = CommonUtil.getValue(traceSampling, engineConfigService, EngineConfigService::getLogSimpling);
                break;
            default:
                traceSampling = CommonUtil.getValue(traceSampling, engineConfigService, EngineConfigService::getLogSimpling);
                break;
        }
        po.setTraceSampling(traceSampling);
        return po;
    }

    @Override
    public PressureTaskEntity getRunningTaskBySceneId(Long sceneId, PressureSceneEnum sceneType) {
        if (null == sceneId || null == sceneType) {
            return null;
        }
        PressureTaskQueryParam query = new PressureTaskQueryParam();
        query.setSceneId(sceneId);
        query.setSceneType(sceneType);
        query.setStatuses(Lists.newArrayList(0, 1));
        query.setCurrent(1L);
        query.setSize(1L);
        Page<PressureTaskEntity> page = pressureTaskDao.query(query);
        if (null != page && CollectionUtils.isNotEmpty(page.getRecords())) {
            return page.getRecords().get(0);
        }
        return null;
    }

    @Override
    public int add(PressureTaskPo po) {
        if (null == po) {
            return 0;
        }
        PressureTaskEntity entity = PressureTaskConvertor.INSTANCE.of(po);
        int n = pressureTaskDao.insert(entity);
        if (n > 0) {
            po.setId(entity.getId());
        }
        return n;
    }

    @Override
    public int update(PressureTaskEntity entity) {
        return pressureTaskDao.update(entity);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        return pressureTaskDao.updateStatus(id, status);
    }

    @Override
    public int delete(Long id) {
        return pressureTaskDao.delete(id);
    }

    @Override
    public PressureTaskEntity getById(Long id) {
        return pressureTaskDao.getById(id);
    }

    @Override
    public PressureTaskQueryParam query(PressureTaskQueryParam param) {
        return pressureTaskDao.query(param);
    }

}
