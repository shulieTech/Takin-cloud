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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ??????
 * @date 2020-04-27
 */
@Slf4j
@RestController
@Api(tags = "????????????", value = "????????????")
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
    @ApiOperation(value = "??????????????????")
    public String taskResultNotify(SceneTaskNotifyParam notify) {
        notify.setTenantId(notify.getTenantId() == null ? notify.getCustomerId() : notify.getTenantId());
        return sceneTaskService.taskResultNotify(notify);
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_TASK_INIT_CALL_BACK)
    @ApiOperation(value = "???????????????????????????")
    public ResponseResult<Object> initCallback(ScheduleInitParamExt param) {
        // ???????????????
        scheduleService.initScheduleCallback(param);
        return ResponseResult.success();
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_FILE_CONTACT)
    @ApiModelProperty(value = "?????????????????????")
    public ResponseResult<Object> preSplitFile(@RequestBody FileSplitResultVO resultVO) {
        try {
            SceneContactFileOutput output = fileSliceService.contactScene(new SceneBigFileSliceParam()
                .setFileName(resultVO.getFileName())
                .setSceneId(resultVO.getSceneId())
                .setIsSplit(resultVO.getIsSplit())
                .setIsOrderSplit(resultVO.getIsOrderSplit())
            );
            return ResponseResult.success(output);
        } catch (TakinCloudException e) {
            log.error("????????????????????????????????????", e);
            return ResponseResult.fail("????????????????????????????????????", e.getMessage());
        }
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_START)
    @ApiOperation(value = "??????????????????")
    public ResponseResult<SceneActionResp> start(@RequestBody SceneTaskStartReq request) {
        // ????????????????????????
        CloudPluginUtils.fillUserData(request);
        SceneTaskStartInput input = BeanUtil.copyProperties(request, SceneTaskStartInput.class);
        // ??????????????????
        input.setOperateId(request.getUserId());
        // ????????????
        SceneActionOutput output = sceneTaskService.start(input);
        SceneActionResp resp = BeanUtil.copyProperties(output, SceneActionResp.class);
        return ResponseResult.success(resp);
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_START_FLOW_DEBUG)
    @ApiOperation(value = "????????????????????????")
    ResponseResult<Long> startFlowDebugTask(@RequestBody TaskFlowDebugStartReq taskFlowDebugStartReq) {
        SceneManageWrapperInput input = SceneTaskOpenConverter.INSTANCE.ofTaskDebugDataStartReq(taskFlowDebugStartReq);
        // ????????????
        input.setOperateId(input.getUserId());
        input.setOperateName(input.getUserName());
        //????????????????????????????????????id??????????????? modified by xr.l 20210712
        List<EnginePluginInput> enginePluginInputs = null;
        if (CollectionUtils.isNotEmpty(taskFlowDebugStartReq.getEnginePlugins())) {
            List<EnginePluginsRefOpen> enginePlugins = taskFlowDebugStartReq.getEnginePlugins();
            enginePluginInputs = enginePlugins.stream().map(plugin -> new EnginePluginInput()
                .setPluginId(plugin.getPluginId())
                .setPluginVersion(plugin.getVersion())
            ).collect(Collectors.toList());
        }
        Long reportId = sceneTaskService.startFlowDebugTask(input, enginePluginInputs);
        return ResponseResult.success(reportId);
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_START_INSPECT)
    @ApiOperation(value = "??????????????????")
    ResponseResult<SceneInspectTaskStartResp> startInspectTask(@RequestBody TaskInspectStartReq taskFlowDebugStartReq) {
        SceneManageWrapperInput input = SceneTaskOpenConverter.INSTANCE.ofTaskInspectStartReq(taskFlowDebugStartReq);

        //????????????????????????????????????id??????????????? modified by xr.l 20210712
        List<EnginePluginInput> enginePluginInputs = null;
        if (CollectionUtils.isNotEmpty(taskFlowDebugStartReq.getEnginePlugins())) {
            List<EnginePluginsRefOpen> enginePlugins = taskFlowDebugStartReq.getEnginePlugins();
            enginePluginInputs = enginePlugins.stream().map(plugin -> new EnginePluginInput()
                .setPluginId(plugin.getPluginId())
                .setPluginVersion(plugin.getVersion())
            ).collect(Collectors.toList());
        }
        SceneInspectTaskStartOutput output = sceneTaskService.startInspectTask(input, enginePluginInputs);
        SceneInspectTaskStartResp startResp = BeanUtil.copyProperties(output, SceneInspectTaskStartResp.class);
        return ResponseResult.success(startResp);
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_FORCE_STOP_INSPECT)
    @ApiOperation(value = "???????????????????????????????????????????????????????????????")
    ResponseResult<SceneTaskStopResp> forceStopTask(@RequestBody TaskStopReq req) {
        SceneTaskStopOutput output = sceneTaskService.forceStopTask(req.getReportId(), req.isFinishReport());
        SceneTaskStopResp stopResp = BeanUtil.copyProperties(output, SceneTaskStopResp.class);
        return ResponseResult.success(stopResp);
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_STOP_INSPECT)
    @ApiOperation(value = "??????????????????")
    ResponseResult<SceneInspectTaskStopResp> stopInspectTask(@RequestBody TaskInspectStopReq taskFlowDebugStopReq) {
        SceneInspectTaskStopOutput output = sceneTaskService.stopInspectTask(taskFlowDebugStopReq.getSceneId());
        SceneInspectTaskStopResp stopResp = BeanUtil.copyProperties(output, SceneInspectTaskStopResp.class);
        return ResponseResult.success(stopResp);
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_UPDATE_TPS)
    @ApiOperation(value = "??????????????????tps")
    ResponseResult<String> updateSceneTaskTps(@RequestBody SceneTaskUpdateTpsReq sceneTaskUpdateTpsReq) {

        SceneTaskUpdateTpsInput input = new SceneTaskUpdateTpsInput();
        input.setSceneId(sceneTaskUpdateTpsReq.getSceneId());
        input.setReportId(sceneTaskUpdateTpsReq.getReportId());
        input.setTpsNum(sceneTaskUpdateTpsReq.getTpsNum());
        input.setXpathMd5(sceneTaskUpdateTpsReq.getXpathMd5());
        sceneTaskService.updateSceneTaskTps(input);
        return ResponseResult.success("tps????????????");
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_TASK_ADJUST_TPS)
    @ApiOperation(value = "?????????????????????tps")
    ResponseResult<SceneTaskAdjustTpsResp> queryAdjustTaskTps(@RequestParam Long sceneId, @RequestParam Long reportId, @RequestParam String xpathMd5) {

        SceneTaskQueryTpsInput input = new SceneTaskQueryTpsInput();
        input.setSceneId(sceneId);
        input.setReportId(reportId);
        input.setXpathMd5(xpathMd5);
        Double value = sceneTaskService.queryAdjustTaskTps(input);
        SceneTaskAdjustTpsResp sceneTaskAdjustTpsResp = new SceneTaskAdjustTpsResp();
        sceneTaskAdjustTpsResp.setTotalTps(value.longValue());
        return ResponseResult.success(sceneTaskAdjustTpsResp);
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_TASK_CHECK_TASK)
    @ApiOperation(value = "??????????????????")
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
    @ApiOperation(value = "??????????????????")
    public ResponseResult<SceneTryRunTaskStartResp> startTryRunTask(@RequestBody
    SceneTryRunTaskStartReq sceneTryRunTaskStartReq) {
        SceneManageWrapperInput input = SceneTaskOpenConverter.INSTANCE.ofSceneTryRunTaskReq(sceneTryRunTaskStartReq);
        // ????????????
        CloudPluginUtils.fillUserData(input);
        input.setOperateId(input.getUserId());
        input.setOperateName(input.getUserName());
        //????????????????????????????????????id??????????????? modified by xr.l 20210712
        List<EnginePluginInput> enginePluginInputs = null;
        if (CollectionUtils.isNotEmpty(sceneTryRunTaskStartReq.getEnginePlugins())) {
            List<EnginePluginsRefOpen> enginePlugins = sceneTryRunTaskStartReq.getEnginePlugins();
            enginePluginInputs = enginePlugins.stream().map(plugin -> new EnginePluginInput()
                .setPluginId(plugin.getPluginId())
                .setPluginVersion(plugin.getVersion())
            ).collect(Collectors.toList());
        }
        SceneTryRunTaskStartOutput output = sceneTaskService.startTryRun(input, enginePluginInputs);
        SceneTryRunTaskStartResp response = BeanUtil.copyProperties(output, SceneTryRunTaskStartResp.class);
        return ResponseResult.success(response);
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_TASK_CALL_BACK_TO_WRITE_BALANCE)
    @ApiOperation(value = "????????????????????????????????????")
    public ResponseResult<SceneStartCheckResp> writeBalance(AssetBalanceExt balanceExt) {
        sceneTaskService.writeBalance(balanceExt);
        return ResponseResult.success();
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_TASK_CHECK_STATUS)
    @ApiOperation(value = "??????????????????")
    public ResponseResult<SceneTryRunTaskStatusResp> checkTaskStatus(@RequestParam Long sceneId, @RequestParam Long reportId) {
        SceneTryRunTaskStatusOutput output = sceneTaskService.checkTaskStatus(sceneId, reportId);
        SceneTryRunTaskStatusResp resp = BeanUtil.copyProperties(output, SceneTryRunTaskStatusResp.class);
        return ResponseResult.success(resp);
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_TASK_CHECK_JOB_STATUS)
    @ApiOperation(value = "???????????????????????????????????????")
    public ResponseResult<SceneJobStateResp> checkJobStatus(Long id) {
        SceneJobStateOutput jobState = sceneTaskService.checkSceneJobStatus(id);
        SceneJobStateResp resp = new SceneJobStateResp();
        resp.setState(jobState.getState());
        resp.setMsg(jobState.getMsg());
        return ResponseResult.success(resp);
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_TASK_START_PRE_CHECK)
    @ApiOperation(value = "???????????????????????????")
    public ResponseResult<SceneStartCheckResp> sceneStartPreCheck(@RequestParam Long sceneId) {
        SceneTaskStartCheckInput input = new SceneTaskStartCheckInput();
        input.setSceneId(sceneId);
        SceneTaskStartCheckOutput output = sceneTaskService.sceneStartCsvPositionCheck(input);
        SceneStartCheckResp resp = BeanUtil.copyProperties(output, SceneStartCheckResp.class);
        return ResponseResult.success(resp);
    }

    /**
     * ??????????????????????????????
     * <p></[>update t_scene_manage set `status`=0 where id=???;</p>
     * <p>update t_report set `status`=2 where scene_id=???;</p>
     *
     * @param paramMap ??????
     * @return -
     */
    @PutMapping(EntrypointUrl.METHOD_SCENE_TASK_RESUME)
    public ResponseResult<String> resumeSceneTask(@RequestBody Map<String, Object> paramMap) {
        if (paramMap == null || paramMap.get("sceneId") == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_RUNNING_PARAM_VERIFY_ERROR, "sceneId cannot be null");
        }
        Long sceneId = Long.parseLong(String.valueOf(paramMap.get("sceneId")));
        int rows = reportDao.updateStatus(sceneId, 2);
        log.debug("resumeSceneTask ????????????:{}", rows);
        sceneManageDao.updateStatus(sceneId, 0);
        return ResponseResult.success("resume success");
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_STOP)
    @ApiOperation(value = "??????????????????")
    public ResponseResult<String> stop(@RequestBody SceneManageIdReq req) {
        //?????????sla?????????
        if (req.getReportId() != null) {
            UpdateReportSlaDataInput slaDataInput = new UpdateReportSlaDataInput();
            slaDataInput.setReportId(req.getReportId());
            slaDataInput.setSlaBean(BeanUtil.copyProperties(req.getSlaBean(), SlaBean.class));
            reportService.updateReportSlaData(slaDataInput);
        }
        log.info("??????{}-{} ????????????web ??? cloud ????????????", req.getId(), req.getReportId());
        // ???sla??????????????????
        sceneTaskService.stop(req.getId());
        return ResponseResult.success("??????????????????");
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_TASK_BOLT_STOP)
    @ApiOperation(value = "??????????????????")
    public ResponseResult<Integer> boltStop(@RequestBody SceneManageIdReq request) {
        try {
            return ResponseResult.success(sceneTaskService.blotStop(request.getId()));
        } catch (Exception ex) {
            return ResponseResult.fail(ex.getMessage(), null);
        }
    }

}
