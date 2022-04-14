package io.shulie.takin.cloud.entrypoint.controller.scene.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import io.shulie.takin.cloud.biz.cache.DictionaryCache;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneSlaRefInput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.service.report.ReportService;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.biz.utils.SlaUtil;
import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOpitons;
import io.shulie.takin.cloud.common.constants.DicKeyConstant;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.entrypoint.convert.SceneBusinessActivityRefRespConvertor;
import io.shulie.takin.cloud.entrypoint.convert.SceneManageRespConvertor;
import io.shulie.takin.cloud.entrypoint.convert.SceneScriptRefRespConvertor;
import io.shulie.takin.cloud.entrypoint.convert.SceneSlaRefRespConvertor;
import io.shulie.takin.cloud.ext.content.enginecall.ThreadGroupConfigExt;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.sdk.model.common.TimeBean;
import io.shulie.takin.cloud.sdk.model.common.TimeUnitEnum;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.BusinessActivityDetailResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.BusinessActivityDetailResponse;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneDetailResponse;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneDetailResponse.ScriptDetailResponse;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneDetailResponse.SlaDetailResponse;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResponse;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResponse.SceneSlaRefResponse;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianshui
 * @date 2020/4/17 下午2:31
 */
@RestController
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_SCENE_MANAGE)
@Api(tags = "压测场景管理")
public class SceneManageDetailController {
    @Resource(type = ReportService.class)
    ReportService reportService;
    @Resource(type = DictionaryCache.class)
    private DictionaryCache dictionaryCache;
    @Resource(type = SceneManageService.class)
    private SceneManageService sceneManageService;

    /**
     * 供编辑使用
     */
    @ApiOperation(value = "压测场景编辑详情")
    @GetMapping(EntrypointUrl.METHOD_SCENE_MANAGE_DETAIL)
    public ResponseResult<SceneManageWrapperResponse> getDetailForEdit(
        @ApiParam(name = "id", value = "ID") Long id,
        @ApiParam(name = "reportId", value = "reportId") Long reportId) {
        if (reportId != null && reportId != 0) {
            ReportResult reportBaseInfo = reportService.getReportBaseInfo(reportId);
            if (reportBaseInfo != null) {
                id = reportBaseInfo.getSceneId();
            } else {
                throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "报告不存在:" + reportId);
            }
        }
        SceneManageQueryOpitons options = new SceneManageQueryOpitons();
        options.setIncludeBusinessActivity(true);
        options.setIncludeScript(true);
        options.setIncludeSLA(true);

        try {
            SceneManageWrapperOutput sceneManage = sceneManageService.getSceneManage(id, options);
            assembleFeatures2(sceneManage);
            return wrapperSceneManage(sceneManage);
        } catch (TakinCloudException exception) {
            return ResponseResult.fail(TakinCloudExceptionEnum.REPORT_GET_ERROR.getErrorCode(), exception.getMessage(), "");
        }
    }

    /**
     * 无租户版本
     */
    @ApiOperation(value = "压测场景编辑详情-无租户")
    @GetMapping(EntrypointUrl.METHOD_SCENE_MANAGE_DETAIL_NO_AUTH)
    public ResponseResult<SceneManageWrapperResponse> getDetailNoAuth(
        @ApiParam(name = "id", value = "ID") Long id,
        @ApiParam(name = "reportId", value = "reportId") Long reportId) {
        return getDetailForEdit(id, reportId);
    }

    public void assembleFeatures2(SceneManageWrapperOutput resp) {
        String features = resp.getFeatures();
        if (StringUtils.isBlank(features)) {
            return;
        }
        HashMap<String, Object> map = JSON.parseObject(features, HashMap.class);
        Integer configType = -1;
        if (map.containsKey(SceneManageConstant.FEATURES_CONFIG_TYPE)) {
            configType = (Integer)map.get(SceneManageConstant.FEATURES_CONFIG_TYPE);
            resp.setConfigType(configType);
        }
        if (map.containsKey(SceneManageConstant.FEATURES_SCRIPT_ID)) {
            Long scriptId = Long.valueOf(map.get(SceneManageConstant.FEATURES_SCRIPT_ID).toString());
            if (configType == 1) {
                //业务活动
                List<SceneManageWrapperOutput.SceneBusinessActivityRefOutput> businessActivityConfig = resp
                    .getBusinessActivityConfig();
                for (SceneManageWrapperOutput.SceneBusinessActivityRefOutput data : businessActivityConfig) {
                    data.setScriptId(scriptId);
                    //业务活动的脚本id也在外面放一份
                    resp.setScriptId(scriptId);
                }
            } else {
                resp.setScriptId(scriptId);
            }
        }
        Object businessFlowId = map.get(SceneManageConstant.FEATURES_BUSINESS_FLOW_ID);
        resp.setBusinessFlowId(businessFlowId == null ? null : Long.valueOf(businessFlowId.toString()));

        // 新版本
        if (StrUtil.isNotBlank(resp.getScriptAnalysisResult())) {
            if (map.containsKey("dataValidation")) {
                JSONObject dataValidation = (JSONObject)map.get("dataValidation");
                Integer scheduleInterval = (Integer)dataValidation.get("timeInterval");
                resp.setScheduleInterval(scheduleInterval);
            }
        }
        //旧版本
        else {
            if (map.containsKey(SceneManageConstant.FEATURES_SCHEDULE_INTERVAL)) {
                Integer schedualInterval = (Integer)map.get(SceneManageConstant.FEATURES_SCHEDULE_INTERVAL);
                resp.setScheduleInterval(schedualInterval);
            }
        }
    }

    /**
     * 供详情使用
     */
    @GetMapping(EntrypointUrl.METHOD_SCENE_MANAGE_CONTENT)
    @ApiOperation(value = "压测场景详情")
    public ResponseResult<SceneDetailResponse> getContent(@ApiParam(value = "id") Long id) {
        ResponseResult<SceneManageWrapperResponse> resDTO = getDetailForEdit(id, 0L);
        if (!resDTO.getSuccess()) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, resDTO.getError().getMsg());
        }
        return ResponseResult.success(convertSceneManageWrapper2SceneDetail(resDTO.getData()));
    }

    private SceneDetailResponse convertSceneManageWrapper2SceneDetail(SceneManageWrapperResponse wrapperDTO) {
        SceneDetailResponse detailDTO = new SceneDetailResponse();
        //基本信息
        detailDTO.setId(wrapperDTO.getId());
        detailDTO.setSceneName(wrapperDTO.getPressureTestSceneName());

        detailDTO.setUpdateTime(wrapperDTO.getUpdateTime());
        detailDTO.setLastPtTime(wrapperDTO.getLastPtTime());
        detailDTO.setStatus(BeanUtil.copyProperties(
            dictionaryCache.getObjectByParam(DicKeyConstant.SCENE_MANAGE_STATUS, wrapperDTO.getStatus())
            , io.shulie.takin.cloud.sdk.model.common.EnumResult.class));
        //业务活动
        if (CollectionUtils.isNotEmpty(wrapperDTO.getBusinessActivityConfig())) {
            List<BusinessActivityDetailResp> activity = Lists.newArrayList();
            wrapperDTO.getBusinessActivityConfig().forEach(data -> {
                BusinessActivityDetailResponse dto = new BusinessActivityDetailResponse();
                dto.setBusinessActivityId(data.getBusinessActivityId());
                dto.setBusinessActivityName(data.getBusinessActivityName());
                dto.setTargetTPS(data.getTargetTPS());
                dto.setTargetRT(data.getTargetRT());
                dto.setTargetSuccessRate(data.getTargetSuccessRate());
                dto.setTargetSA(data.getTargetSA());
                activity.add(BeanUtil.copyProperties(dto, BusinessActivityDetailResp.class));
            });
            detailDTO.setBusinessActivityConfig(activity);
        }
        //施压配置
        detailDTO.setConcurrenceNum(wrapperDTO.getConcurrenceNum());
        detailDTO.setIpNum(wrapperDTO.getIpNum());
        detailDTO.setPressureMode(BeanUtil.copyProperties(
            dictionaryCache.getObjectByParam(DicKeyConstant.PT_MODEL, wrapperDTO.getPressureMode())
            , io.shulie.takin.cloud.sdk.model.common.EnumResult.class));
        detailDTO.setPressureTestTime(wrapperDTO.getPressureTestTime());
        detailDTO.setIncreasingTime(wrapperDTO.getIncreasingTime());
        detailDTO.setStep(wrapperDTO.getStep());
        detailDTO.setEstimateFlow(wrapperDTO.getEstimateFlow());
        //上传文件
        if (CollectionUtils.isNotEmpty(wrapperDTO.getUploadFile())) {
            List<ScriptDetailResponse> script = Lists.newArrayList();
            wrapperDTO.getUploadFile().forEach(data -> {
                ScriptDetailResponse dto = new ScriptDetailResponse();
                dto.setFileName(data.getFileName());
                dto.setUploadTime(data.getUploadTime());
                dto.setFileType(BeanUtil.copyProperties(
                    dictionaryCache.getObjectByParam(DicKeyConstant.FILE_TYPE, data.getFileType())
                    , io.shulie.takin.cloud.sdk.model.common.EnumResult.class));
                dto.setUploadedData(data.getUploadedData());
                dto.setIsSplit(BeanUtil.copyProperties(dictionaryCache.getObjectByParam(DicKeyConstant.IS_DELETED, data.getIsSplit())
                    , io.shulie.takin.cloud.sdk.model.common.EnumResult.class));
                script.add(dto);
            });
            detailDTO.setUploadFile(script);
        }

        //SLA配置
        if (CollectionUtils.isNotEmpty(wrapperDTO.getStopCondition())) {
            List<SlaDetailResponse> sla = Lists.newArrayList();
            wrapperDTO.getStopCondition().forEach(data -> {
                SlaDetailResponse stop = new SlaDetailResponse();
                stop.setRuleName(data.getRuleName());
                stop.setBusinessActivity(
                    convertIdsToNames(data.getBusinessActivity(), detailDTO.getBusinessActivityConfig()));
                stop.setRule(buildRule(data));
                stop.setStatus(BeanUtil.copyProperties(dictionaryCache.getObjectByParam(DicKeyConstant.LIVE_STATUS, data.getStatus())
                    , io.shulie.takin.cloud.sdk.model.common.EnumResult.class));
                sla.add(stop);
            });
            detailDTO.setStopCondition(sla);
        }

        if (CollectionUtils.isNotEmpty(wrapperDTO.getWarningCondition())) {
            List<SlaDetailResponse> sla = Lists.newArrayList();
            wrapperDTO.getWarningCondition().forEach(data -> {
                SlaDetailResponse stop = new SlaDetailResponse();
                stop.setRuleName(data.getRuleName());
                stop.setBusinessActivity(
                    convertIdsToNames(data.getBusinessActivity(), detailDTO.getBusinessActivityConfig()));
                stop.setRule(buildRule(data));
                stop.setStatus(BeanUtil.copyProperties(
                    dictionaryCache.getObjectByParam(DicKeyConstant.LIVE_STATUS, data.getStatus())
                    , io.shulie.takin.cloud.sdk.model.common.EnumResult.class));
                sla.add(stop);
            });
            detailDTO.setWarningCondition(sla);
        }

        return detailDTO;
    }

    private String convertIdsToNames(String[] ids, List<BusinessActivityDetailResp> detailList) {
        if (ids == null || ids.length == 0 || CollectionUtils.isEmpty(detailList)) {
            return null;
        }

        Map<String, String> detailMap = detailList.stream().collect(Collectors.toMap(t ->
                String.valueOf(t.getBusinessActivityId()),
            BusinessActivityDetailResp::getBusinessActivityName));

        StringBuilder sb = new StringBuilder();
        for (String id : ids) {
            if ("-1".equals(id)) {
                sb.append("所有");
            } else {
                sb.append(detailMap.get(id));
            }
            sb.append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    private String buildRule(SceneSlaRefResponse slaRefDTO) {
        SceneSlaRefInput input = BeanUtil.copyProperties(slaRefDTO, SceneSlaRefInput.class);
        Map<String, Object> dataMap = SlaUtil.matchCondition(input, new SendMetricsEvent());
        return String.valueOf(dataMap.get("type"))
            + dataMap.get("compare")
            + slaRefDTO.getRule().getDuring()
            + dataMap.get("unit")
            + "连续出现"
            + slaRefDTO.getRule().getTimes()
            + "次";
    }

    private ResponseResult<SceneManageWrapperResponse> wrapperSceneManage(SceneManageWrapperOutput sceneManage) {

        SceneManageWrapperResponse response = SceneManageRespConvertor.INSTANCE.of(sceneManage);

        response.setBusinessActivityConfig(
            SceneBusinessActivityRefRespConvertor.INSTANCE.ofList(sceneManage.getBusinessActivityConfig()));
        response.setStopCondition(SceneSlaRefRespConvertor.INSTANCE.ofList(sceneManage.getStopCondition()));
        response.setWarningCondition(SceneSlaRefRespConvertor.INSTANCE.ofList(sceneManage.getWarningCondition()));
        response.setUploadFile(SceneScriptRefRespConvertor.INSTANCE.ofList(sceneManage.getUploadFile()));
        response.setScheduleInterval(sceneManage.getScheduleInterval());

        if (CollectionUtils.isEmpty(response.getBusinessActivityConfig())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, "场景不存在或者没有业务活动配置");
        }
        sceneManage.getBusinessActivityConfig().forEach(data -> {
            if (StringUtils.isBlank(data.getBindRef())) {
                data.setBindRef("-1");
            }
            if (StringUtils.isBlank(data.getApplicationIds())) {
                data.setApplicationIds("-1");
            }
        });
        //旧版
        if (StringUtils.isBlank(sceneManage.getScriptAnalysisResult())) {
            Map<String, ThreadGroupConfigExt> map = sceneManage.getThreadGroupConfigMap();
            if (null != map) {
                ThreadGroupConfigExt tgConfig = map.get("all");
                if (null != tgConfig) {
                    response.setPressureMode(tgConfig.getMode());
                    response.setStep(tgConfig.getSteps());
                    if (null != tgConfig.getRampUp()) {
                        long time = TimeUnit.MINUTES.convert(tgConfig.getRampUp().longValue(),
                            TimeUnitEnum.value(tgConfig.getRampUpUnit()).getUnit());
                        TimeBean increasingTime = new TimeBean(time, TimeUnitEnum.MINUTE.getValue());
                        response.setIncreasingSecond(increasingTime.getSecondTime());
                        response.setIncreasingTime(increasingTime);
                    }
                }
            }
        }
        return ResponseResult.success(response);
    }
}
