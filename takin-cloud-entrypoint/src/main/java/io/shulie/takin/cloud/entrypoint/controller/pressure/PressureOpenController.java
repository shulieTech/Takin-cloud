package io.shulie.takin.cloud.entrypoint.controller.pressure;

import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.biz.pojo.PressureTaskPo;
import io.shulie.takin.cloud.biz.service.engine.EngineService;
import io.shulie.takin.cloud.biz.service.pressure.PressureTaskService;
import io.shulie.takin.cloud.biz.service.schedule.impl.FileSplitService;
import io.shulie.takin.cloud.biz.service.strategy.StrategyConfigService;
import io.shulie.takin.cloud.common.constants.FileConstants;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.common.utils.FileUtils;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;
import io.shulie.takin.cloud.ext.api.AssetExtApi;
import io.shulie.takin.cloud.ext.content.asset.AccountInfoExt;
import io.shulie.takin.cloud.ext.content.enginecall.EngineRunConfig;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStartRequestExt;
import io.shulie.takin.cloud.ext.content.enginecall.StrategyConfigExt;
import io.shulie.takin.cloud.ext.helper.CommonHelper;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.sdk.model.request.pressure.StartEngineReq;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneScriptRefOpen;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: liyuanba
 * @Date: 2021/12/27 5:46 下午
 */
@Slf4j
@RestController
@Api(tags = "压测相关接口", value = "压测相关接口")
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_PRESSURE)
public class PressureOpenController {
    @Autowired
    private PressureTaskService pressureTaskService;
    @Autowired
    private EngineService engineService;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private PluginManager pluginManager;
    @Autowired
    private FileSplitService fileSplitService;
    @Autowired
    private StrategyConfigService strategyConfigService;


    @ApiOperation(value = "启动压测")
    @PostMapping(EntrypointUrl.METHOD_PRESSURE_START)
    @ResponseBody
    public ResponseResult<?> start(StartEngineReq req) {
        log.info("req="+JsonUtil.toJson(req));
        //部分参数默认初始化
        init(req);
        //参数校验
        ResponseResult<?> checkResult = checkStartParams(req);
        if (null != checkResult && BooleanUtils.isNotTrue(checkResult.getSuccess())) {
            return checkResult;
        }
        //构建压测任务
        PressureTaskPo task = pressureTaskService.buildPressureTask(req);
        //资源检测
        int cpuMemoryCheck = engineService.check(task.getPodNum(), task.getRequestCpu(), task.getRequestMemory(), task.getLimitCpu(), task.getLimitMemory());
        if (0 != cpuMemoryCheck) {
            String msg = 1 == cpuMemoryCheck ? "CPU资源不足" : "内存资源不足";
            return ResponseResult.fail("2", msg, "当前忙，请稍后再试");
        }
        if (pressureTaskService.add(task) <= 0) {
            log.error("task="+ JsonUtil.toJson(task));
            return ResponseResult.fail("3", "创建任务失败", "请联系管理员处理");
        }
        //获取配置
        StrategyConfigExt strategyConfig = strategyConfigService.getCurrentStrategyConfig();
        //构建压测引擎启动配置
        EngineRunConfig config = pressureTaskService.buildEngineRunConfig(task, strategyConfig);
        String delimiter = CommonUtil.getValue("@—@", strategyConfig, StrategyConfigExt::getDelimiter);
        //文件拆分
        PressureSceneEnum sceneType = PressureSceneEnum.value(req.getSceneType());
        List<ScheduleStartRequestExt.DataFile> files = fileSplitService.generateFileSlice(config.getFileSets(),
                config.getSceneId(), sceneType, config.getTaskId(), config.getPodCount(), delimiter, task.getContinueRead());
        //复制文件到任务目录下
        if (CollectionUtils.isNotEmpty(files)) {
            String targetDir = FileUtils.mergePaths(appConfig.getScriptPath(), FileConstants.RUNNING_SCRIPT_FILE_DIR, task.getId().toString());
            files = files.stream().filter(Objects::nonNull)
                    .peek(f -> f.setPath(FileUtils.copy(f.getPath(), targetDir)))
                    .filter(f -> StringUtils.isNotBlank(f.getPath()))
                    .collect(Collectors.toList());
        }
        config.setFileSets(files);

        return engineService.start(config);
    }

    /**
     * 初始化入参
     */
    private void init(StartEngineReq req) {
        if (null == req.getPodNum()) {
            req.setPodNum(1);
        }
        if (CollectionUtils.isNotEmpty(req.getUploadFiles())) {
            req.getUploadFiles().stream().filter(Objects::nonNull)
                    .forEach(f -> f.setUploadPath(CommonHelper.mergeDirPath(appConfig.getScriptPath(), f.getUploadPath())));
        }
    }

    private ResponseResult<?> checkStartParams(StartEngineReq req) {
        // 流量判断
        if (null == req.getTenantId()) {
            return ResponseResult.fail("1", "任务启动校验异常！", "场景没有绑定客户信息");
        }
        if (null == req.getPodNum()) {
            return ResponseResult.fail("1", "启动pod数量为空", "请传入启动pod数");
        }
        BigDecimal minVum = null;
        PressureSceneEnum sceneType = PressureSceneEnum.value(req.getSceneType());
        if (null == sceneType) {
            return ResponseResult.fail("1", "压测场景类型值非法", "请传入有效值");
        }
        switch (sceneType) {
            //脚本调试好像不需要场景id
            case TRY_RUN:
                if (null == req.getThroughput() || req.getThroughput() <= 0) {
                    return ResponseResult.fail("1", "并发数为空或值非法", "请传入一个大于0的整数");
                }
                if (null == req.getLoopsNum() || req.getLoopsNum() <= 0) {
                    return ResponseResult.fail("1", "循环次数为空或值非法", "请传入一个大于0的整数");
                }
                minVum = BigDecimal.ONE;;
                break;
            //流量调试好像不需要场景id
            case FLOW_DEBUG:
                minVum = BigDecimal.ONE;
                break;
            case INSPECTION_MODE:
                minVum = BigDecimal.ONE;
                if (null == req.getId()) {
                    return ResponseResult.fail("1", "场景ID不能为空", "请传入场景ID");
                } else if (0 >= req.getId()) {
                    return ResponseResult.fail("1", "场景ID值非法", "请参入一个大于0的整数");
                }
                if (null == req.getFixTimer() || req.getFixTimer() <= 0) {
                    return ResponseResult.fail("1", "巡检间隔时间为空或值非法", "请参入一个大于0的整数");
                }
                break;
            default:
                minVum = new BigDecimal(100);
                if (null == req.getId()) {
                    return ResponseResult.fail("1", "场景ID不能为空", "请传入场景ID");
                } else if (0 >= req.getId()) {
                    return ResponseResult.fail("1", "场景ID值非法", "请传入一个大于0的值");
                }
                if (null == req.getBusinessActivityConfig()) {
                    return ResponseResult.fail("1", "业务活动配置为空", "请传入业务活动配置信息");
                }
                if (null == req.getHoldTime()) {
                    return ResponseResult.fail("1", "压测时长为空", "请传入压测时长");
                }
                if (StringUtils.isBlank(req.getHoldTimeUnit())) {
                    return ResponseResult.fail("1", "压测时长单位为空", "请传入压测时长单位");
                }
                break;
        }
        boolean haveScriptFile = false;
        for (SceneScriptRefOpen file : req.getUploadFiles()) {
            if (StringUtils.isBlank(file.getFileName()) || StringUtils.isBlank(file.getUploadPath())) {
                return ResponseResult.fail("1", "上传文件名为空", "请检测上传参数");
            }
            if (!FileUtils.isExists(file.getUploadPath())) {
                return ResponseResult.fail("1", "文件不存在："+file.getUploadPath(), "请传入业务活动配置信息");
            }
            if (!checkFileMd5(file)) {
                return ResponseResult.fail("1", file.getFileName()+"文件检验失败", "文件发生了变化，请在启动时不要修改文件");
            }

            if (0 == file.getFileType() && file.getFileName().endsWith(FileConstants.SCRIPT_NAME_SUFFIX)) {
                haveScriptFile = true;
            }
        }
        if (!haveScriptFile) {
            return ResponseResult.fail("1", "没有脚本文件", "请传入脚本文件");
        }
        //检测是否已经有任务在跑了
        PressureTaskEntity have = pressureTaskService.getRunningTaskBySceneId(req.getId(), sceneType);
        if (null != have) {
            return ResponseResult.fail("2", "任务已经在运行了！", "请确认当前压测场景状态，不要重复提交");
        }
        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (null != assetExtApi) {
            AccountInfoExt account = assetExtApi.queryAccount(req.getTenantId(), req.getUserId());
            if (null == account || account.getBalance().compareTo(minVum) < 0) {
                return ResponseResult.fail("3", "压测流量不足！", "压测流量不足，请先充值");
            }
        }
        return null;
    }

    private boolean checkFileMd5(SceneScriptRefOpen file) {
        //todo 文件md5检验
        return true;
    }
}
