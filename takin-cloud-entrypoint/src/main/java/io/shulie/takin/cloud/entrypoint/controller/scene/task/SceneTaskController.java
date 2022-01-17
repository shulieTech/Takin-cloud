package io.shulie.takin.cloud.entrypoint.controller.scene.task;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import com.pamirs.takin.entity.domain.vo.report.SceneTaskNotifyParam;
import com.pamirs.takin.entity.domain.vo.scenemanage.FileSplitResultVO;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import io.shulie.takin.cloud.biz.input.report.UpdateReportSlaDataInput;
import io.shulie.takin.cloud.biz.input.scenemanage.EnginePluginInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageWrapperInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskQueryTpsInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartCheckInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskUpdateTpsInput;
import io.shulie.takin.cloud.biz.output.report.SceneInspectTaskStartOutput;
import io.shulie.takin.cloud.biz.output.report.SceneInspectTaskStopOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneContactFileOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneActionOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneJobStateOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTaskStartCheckOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTaskStopOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTryRunTaskStartOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTryRunTaskStatusOutput;
import io.shulie.takin.cloud.biz.service.report.ReportService;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskService;
import io.shulie.takin.cloud.biz.service.schedule.FileSliceService;
import io.shulie.takin.cloud.biz.service.schedule.ScheduleService;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.data.param.scenemanage.SceneBigFileSliceParam;
import io.shulie.takin.cloud.entrypoint.convert.SceneTaskOpenConverter;
import io.shulie.takin.cloud.ext.content.asset.AssetBalanceExt;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleInitParamExt;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.sdk.model.common.SlaBean;
import io.shulie.takin.cloud.sdk.model.request.engine.EnginePluginsRefOpen;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneTaskStartReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.SceneStartCheckResp;
import io.shulie.takin.cloud.sdk.model.request.scenetask.SceneTaskUpdateTpsReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.SceneTryRunTaskStartReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.TaskFlowDebugStartReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.TaskInspectStartReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.TaskInspectStopReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.TaskStopReq;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneInspectTaskStartResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneInspectTaskStopResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneTryRunTaskStartResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneTryRunTaskStatusResp;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneActionResp;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneJobStateResp;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneTaskAdjustTpsResp;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneTaskStopResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 莫问
 * @date 2020-04-27
 */
@Slf4j
@RestController
@Api(tags = "场景任务", value = "场景任务")
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_SCENE_TASK)
public class SceneTaskController {
    @Resource(type = ReportDao.class)
    ReportDao reportDao;
    @Resource(type = ReportService.class)
    ReportService reportService;
    @Resource(type = SceneManageDAO.class)
    SceneManageDAO sceneManageDao;
    @Resource(type = ScheduleService.class)
    ScheduleService scheduleService;
    @Resource(type = SceneTaskService.class)
    SceneTaskService sceneTaskService;
    @Resource(type = FileSliceService.class)
    FileSliceService fileSliceService;

    @GetMapping(EntrypointUrl.METHOD_SCENE_TASK_TASK_RESULT_NOTIFY)
    @ApiOperation(value = "启动结果通知")
    public String taskResultNotify(SceneTaskNotifyParam notify) {
        notify.setTenantId(notify.getTenantId() == null ? notify.getCustomerId() : notify.getTenantId());
        return sceneTaskService.taskResultNotify(notify);
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_TASK_INIT_CALL_BACK)
    @ApiOperation(value = "调度初始化回调函数")
    public ResponseResult<?> initCallback(ScheduleInitParamExt param) {
        // 初始化调度
        scheduleService.initScheduleCallback(param);
        return ResponseResult.success();
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_FILE_CONTACT)
    @ApiModelProperty(value = "大文件关联场景")
    public ResponseResult<?> preSplitFile(@RequestBody FileSplitResultVO resultVO) {
        try {
            SceneContactFileOutput output = fileSliceService.contactScene(new SceneBigFileSliceParam() {{
                setFileName(resultVO.getFileName());
                setSceneId(resultVO.getSceneId());
                setIsSplit(resultVO.getIsSplit());
                setIsOrderSplit(resultVO.getIsOrderSplit());
            }});
            return ResponseResult.success(output);
        } catch (TakinCloudException e) {
            return ResponseResult.fail("关联文件与脚本、场景异常", e.getMessage());
        }
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_START)
    @ApiOperation(value = "开始场景测试")
    public ResponseResult<SceneActionResp> start(@RequestBody SceneTaskStartReq request) {
        // 填充数据溯源信息
        CloudPluginUtils.fillUserData(request);
        SceneTaskStartInput input = new SceneTaskStartInput();
        BeanUtils.copyProperties(request, input);
        // 设置用户主键
        input.setOperateId(request.getUserId());
        // 启动场景
        SceneActionOutput output = sceneTaskService.start(input);
        SceneActionResp resp = new SceneActionResp();
        BeanUtils.copyProperties(output, resp);
        return ResponseResult.success(resp);
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_START_FLOW_DEBUG)
    @ApiOperation(value = "启动调试流量任务")
    ResponseResult<Long> startFlowDebugTask(@RequestBody TaskFlowDebugStartReq taskFlowDebugStartReq) {
        SceneManageWrapperInput input = SceneTaskOpenConverter.INSTANCE.ofTaskDebugDataStartReq(taskFlowDebugStartReq);
        //压测引擎插件需要传入插件id和插件版本 modified by xr.l 20210712
        List<EnginePluginInput> enginePluginInputs = null;
        if (CollectionUtils.isNotEmpty(taskFlowDebugStartReq.getEnginePlugins())) {
            List<EnginePluginsRefOpen> enginePlugins = taskFlowDebugStartReq.getEnginePlugins();
            enginePluginInputs = enginePlugins.stream().map(plugin -> new EnginePluginInput() {{
                setPluginId(plugin.getPluginId());
                setPluginVersion(plugin.getVersion());
            }}).collect(Collectors.toList());
        }
        Long reportId = sceneTaskService.startFlowDebugTask(input, enginePluginInputs);
        return ResponseResult.success(reportId);
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_START_INSPECT)
    @ApiOperation(value = "启动巡检任务")
    ResponseResult<SceneInspectTaskStartResp> startInspectTask(@RequestBody TaskInspectStartReq taskFlowDebugStartReq) {
        SceneManageWrapperInput input = SceneTaskOpenConverter.INSTANCE.ofTaskInspectStartReq(taskFlowDebugStartReq);

        //压测引擎插件需要传入插件id和插件版本 modified by xr.l 20210712
        List<EnginePluginInput> enginePluginInputs = null;
        if (CollectionUtils.isNotEmpty(taskFlowDebugStartReq.getEnginePlugins())) {
            List<EnginePluginsRefOpen> enginePlugins = taskFlowDebugStartReq.getEnginePlugins();
            enginePluginInputs = enginePlugins.stream().map(plugin -> new EnginePluginInput() {{
                setPluginId(plugin.getPluginId());
                setPluginVersion(plugin.getVersion());
            }}).collect(Collectors.toList());
        }
        SceneInspectTaskStartOutput output = sceneTaskService.startInspectTask(input, enginePluginInputs);
        SceneInspectTaskStartResp startResp = new SceneInspectTaskStartResp();
        BeanUtils.copyProperties(output, startResp);
        return ResponseResult.success(startResp);
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_FORCE_STOP_INSPECT)
    @ApiOperation(value = "强制停止任务，提示：可能会造成压测数据丢失")
    ResponseResult<SceneTaskStopResp> forceStopTask(@RequestBody TaskStopReq req) {
        SceneTaskStopOutput output = sceneTaskService.forceStopTask(req.getReportId(), req.isFinishReport());
        SceneTaskStopResp stopResp = new SceneTaskStopResp();
        BeanUtils.copyProperties(output, stopResp);
        return ResponseResult.success(stopResp);
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_STOP_INSPECT)
    @ApiOperation(value = "停止巡检任务")
    ResponseResult<SceneInspectTaskStopResp> stopInspectTask(@RequestBody TaskInspectStopReq taskFlowDebugStopReq) {
        SceneInspectTaskStopOutput output = sceneTaskService.stopInspectTask(taskFlowDebugStopReq.getSceneId());
        SceneInspectTaskStopResp stopResp = new SceneInspectTaskStopResp();
        BeanUtils.copyProperties(output, stopResp);
        return ResponseResult.success(stopResp);
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_UPDATE_TPS)
    @ApiOperation(value = "调整压测任务tps")
    ResponseResult<String> updateSceneTaskTps(@RequestBody SceneTaskUpdateTpsReq sceneTaskUpdateTpsReq) {

        SceneTaskUpdateTpsInput input = new SceneTaskUpdateTpsInput();
        input.setSceneId(sceneTaskUpdateTpsReq.getSceneId());
        input.setReportId(sceneTaskUpdateTpsReq.getReportId());
        input.setTpsNum(sceneTaskUpdateTpsReq.getTpsNum());
        input.setXpathMd5(sceneTaskUpdateTpsReq.getXpathMd5());
        sceneTaskService.updateSceneTaskTps(input);
        return ResponseResult.success("tps更新成功");
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_TASK_ADJUST_TPS)
    @ApiOperation(value = "获取调整的任务tps")
    ResponseResult<SceneTaskAdjustTpsResp> queryAdjustTaskTps(@RequestParam Long sceneId, @RequestParam Long reportId, @RequestParam String xpathMd5) {

        SceneTaskQueryTpsInput input = new SceneTaskQueryTpsInput();
        input.setSceneId(sceneId);
        input.setReportId(reportId);
        input.setXpathMd5(xpathMd5);
        double value = sceneTaskService.queryAdjustTaskTps(input);
        SceneTaskAdjustTpsResp sceneTaskAdjustTpsResp = new SceneTaskAdjustTpsResp();
        sceneTaskAdjustTpsResp.setTotalTps(Double.valueOf(value).longValue());
        return ResponseResult.success(sceneTaskAdjustTpsResp);
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_TASK_CHECK_TASK)
    @ApiOperation(value = "检查启动状态")
    public ResponseResult<SceneActionResp> checkStartStatus(@RequestParam("id") Long id,
        @RequestParam(value = "reportId", required = false) Long reportId) {
        SceneActionOutput sceneAction = sceneTaskService.checkSceneTaskStatus(id, reportId);
        SceneActionResp resp = new SceneActionResp();
        resp.setData(sceneAction.getData());
        resp.setMsg(sceneAction.getMsg());
        resp.setReportId(sceneAction.getReportId());
        return ResponseResult.success(resp);

    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_START_TRY_RUN)
    @ApiOperation(value = "启动脚本试跑")
    public ResponseResult<SceneTryRunTaskStartResp> startTryRunTask(@RequestBody
        SceneTryRunTaskStartReq sceneTryRunTaskStartReq) {
        SceneManageWrapperInput input = SceneTaskOpenConverter.INSTANCE.ofSceneTryRunTaskReq(sceneTryRunTaskStartReq);
        //压测引擎插件需要传入插件id和插件版本 modified by xr.l 20210712
        List<EnginePluginInput> enginePluginInputs = null;
        if (CollectionUtils.isNotEmpty(sceneTryRunTaskStartReq.getEnginePlugins())) {
            List<EnginePluginsRefOpen> enginePlugins = sceneTryRunTaskStartReq.getEnginePlugins();
            enginePluginInputs = enginePlugins.stream().map(plugin -> new EnginePluginInput() {{
                setPluginId(plugin.getPluginId());
                setPluginVersion(plugin.getVersion());
            }}).collect(Collectors.toList());
        }
        SceneTryRunTaskStartOutput sceneTryRunTaskStartOutput = sceneTaskService.startTryRun(input,
            enginePluginInputs);
        SceneTryRunTaskStartResp sceneTryRunTaskStartResp = new SceneTryRunTaskStartResp();
        BeanUtils.copyProperties(sceneTryRunTaskStartOutput, sceneTryRunTaskStartResp);
        return ResponseResult.success(sceneTryRunTaskStartResp);
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_TASK_CALL_BACK_TO_WRITE_BALANCE)
    @ApiOperation(value = "脚本调试回调写入流量账户")
    public ResponseResult<SceneStartCheckResp> writeBalance(AssetBalanceExt balanceExt) {
        sceneTaskService.writeBalance(balanceExt);
        return ResponseResult.success();
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_TASK_CHECK_STATUS)
    @ApiOperation(value = "查询试跑状态")
    public ResponseResult<SceneTryRunTaskStatusResp> checkTaskStatus(@RequestParam Long sceneId, @RequestParam Long reportId) {
        SceneTryRunTaskStatusOutput sceneTryRunTaskStatusOutput = sceneTaskService.checkTaskStatus(sceneId,
            reportId);
        SceneTryRunTaskStatusResp resp = new SceneTryRunTaskStatusResp();
        BeanUtils.copyProperties(sceneTryRunTaskStatusOutput, resp);
        return ResponseResult.success(resp);
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_TASK_CHECK_JOB_STATUS)
    @ApiOperation(value = "检测巡检任务状态：压测引擎")
    public ResponseResult<SceneJobStateResp> checkJobStatus(Long id) {
        SceneJobStateOutput jobState = sceneTaskService.checkSceneJobStatus(id);
        SceneJobStateResp resp = new SceneJobStateResp();
        resp.setState(jobState.getState());
        resp.setMsg(jobState.getMsg());
        return ResponseResult.success(resp);
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_TASK_START_PRE_CHECK)
    @ApiOperation(value = "启动压测前检查位点")
    public ResponseResult<SceneStartCheckResp> sceneStartPreCheck(@RequestParam Long sceneId) {
        SceneTaskStartCheckInput input = new SceneTaskStartCheckInput();
        input.setSceneId(sceneId);
        SceneTaskStartCheckOutput output = sceneTaskService.sceneStartCsvPositionCheck(input);
        SceneStartCheckResp resp = new SceneStartCheckResp();
        BeanUtils.copyProperties(output, resp);
        return ResponseResult.success(resp);
    }

    /**
     * 恢复压测中的场景状态
     * update t_scene_manage set `status`=0 where id=？;
     * update t_report set `status`=2 where scene_id=？;
     *
     * @param paramMap 参数
     * @return -
     */
    @PutMapping(EntrypointUrl.METHOD_SCENE_TASK_RESUME)
    public ResponseResult<String> resumeSceneTask(@RequestBody Map<String, Object> paramMap) {
        if (paramMap == null || paramMap.get("sceneId") == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_RUNNING_PARAM_VERIFY_ERROR, "sceneId cannot be null");
        }
        Long sceneId = Long.parseLong(String.valueOf(paramMap.get("sceneId")));
        int rows = reportDao.updateStatus(sceneId, 2);
        log.debug("resumeSceneTask 影响行数:{}", rows);
        sceneManageDao.updateStatus(sceneId, 0);
        return ResponseResult.success("resume success");
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_STOP)
    @ApiOperation(value = "结束场景测试")
    public ResponseResult<String> stop(@RequestBody SceneManageIdReq req) {
        //记录下sla的数据
        if (req.getReportId() != null) {
            UpdateReportSlaDataInput slaDataInput = new UpdateReportSlaDataInput();
            slaDataInput.setReportId(req.getReportId());
            slaDataInput.setSlaBean(BeanUtil.copyProperties(req.getSlaBean(), SlaBean.class));
            reportService.updateReportSlaData(slaDataInput);
        }
        log.info("任务{}-{} ，原因：web 调 cloud 触发停止", req.getId(), req.getReportId());
        // 与sla操作是一致的
        sceneTaskService.stop(req.getId());
        return ResponseResult.success("停止场景成功");
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_BOLT_STOP)
    @ApiOperation(value = "直接停止场景")
    public ResponseResult<Integer> boltStop(@RequestBody SceneManageIdReq request) {
        try {
            return ResponseResult.success(sceneTaskService.blotStop(request.getId()));
        } catch (Exception ex) {
            return ResponseResult.fail(ex.getMessage(), null);
        }
    }

}
