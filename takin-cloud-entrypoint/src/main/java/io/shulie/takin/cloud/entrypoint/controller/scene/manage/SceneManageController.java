package io.shulie.takin.cloud.entrypoint.controller.scene.manage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import io.shulie.takin.cloud.common.request.scenemanage.UpdateSceneFileRequest;
import io.shulie.takin.cloud.entrypoint.convert.SceneTaskOpenConverter;
import io.shulie.takin.cloud.ext.content.script.ScriptVerityRespExt;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.ScriptCheckAndUpdateReq;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageListResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.ScriptCheckResp;
import io.shulie.takin.cloud.sdk.model.response.strategy.StrategyResp;
import io.shulie.takin.cloud.biz.cache.DictionaryCache;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageQueryInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageWrapperInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneSlaRefInput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageListOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.biz.service.strategy.StrategyConfigService;
import io.shulie.takin.cloud.biz.utils.SlaUtil;
import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOpitons;
import io.shulie.takin.cloud.common.constants.DicKeyConstant;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.ListHelper;
import io.shulie.takin.cloud.entrypoint.convert.SceneBusinessActivityRefRespConvertor;
import io.shulie.takin.cloud.entrypoint.convert.SceneManageReqConvertor;
import io.shulie.takin.cloud.entrypoint.convert.SceneManageRespConvertor;
import io.shulie.takin.cloud.entrypoint.convert.SceneScriptRefRespConvertor;
import io.shulie.takin.cloud.entrypoint.convert.SceneSlaRefRespConvertor;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageDeleteRequest;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageWrapperRequest;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.BusinessActivityDetailResponse;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneDetailResponse;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneDetailResponse.ScriptDetailResponse;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneDetailResponse.SlaDetailResponse;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResponse;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResponse.SceneSlaRefResponse;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.ext.content.enginecall.StrategyOutputExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianshui
 * @date 2020/4/17 下午2:31
 */
@RestController
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_SCENE_MANAGE)
@Api(tags = "压测场景管理")
public class SceneManageController {
    @Resource(type = DictionaryCache.class)
    private DictionaryCache dictionaryCache;
    @Resource(type = SceneManageService.class)
    private SceneManageService sceneManageService;
    @Resource(type = StrategyConfigService.class)
    private StrategyConfigService strategyConfigService;

    @PostMapping(value = EntrypointUrl.METHOD_SCENE_MANAGE_SAVE)
    @ApiOperation(value = "新增压测场景")
    public ResponseResult<Long> add(@RequestBody @Valid SceneManageWrapperRequest wrapperRequest) {
        SceneManageWrapperInput input = SceneManageReqConvertor.INSTANCE.ofSceneManageWrapperInput(wrapperRequest);
        return ResponseResult.success(sceneManageService.addSceneManage(input));
    }

    @PutMapping(value = EntrypointUrl.METHOD_SCENE_MANAGE_UPDATE)
    @ApiOperation(value = "修改压测场景")
    public ResponseResult<?> update(@RequestBody @Valid SceneManageWrapperRequest wrapperRequest) {
        SceneManageWrapperInput input = SceneManageReqConvertor.INSTANCE.ofSceneManageWrapperInput(wrapperRequest);
        sceneManageService.updateSceneManage(input);
        return ResponseResult.success();

    }

    @DeleteMapping(value = EntrypointUrl.METHOD_SCENE_MANAGE_DELETE)
    @ApiOperation(value = "删除压测场景")
    public ResponseResult<?> delete(@RequestBody SceneManageDeleteRequest deleteRequest) {
        sceneManageService.delete(deleteRequest.getId());
        return ResponseResult.success();
    }

    /**
     * 供编辑使用
     */
    @GetMapping(EntrypointUrl.METHOD_SCENE_MANAGE_DETAIL)
    @ApiOperation(value = "压测场景编辑详情")
    public ResponseResult<SceneManageWrapperResponse> getDetailForEdit(@ApiParam(name = "id", value = "ID") Long id) {
        SceneManageQueryOpitons options = new SceneManageQueryOpitons();
        options.setIncludeBusinessActivity(true);
        options.setIncludeScript(true);
        options.setIncludeSLA(true);

        SceneManageWrapperOutput sceneManage = sceneManageService.getSceneManage(id, options);
        assembleFeatures(sceneManage);
        return wrapperSceneManage(sceneManage);
    }

    public void assembleFeatures(SceneManageWrapperOutput resp) {
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

        if (map.containsKey(SceneManageConstant.FEATURES_SCHEDULE_INTERVAL)) {
            Integer scheduleInterval = (Integer)map.get(SceneManageConstant.FEATURES_SCHEDULE_INTERVAL);
            resp.setScheduleInterval(scheduleInterval);
        }
    }

    /**
     * 供详情使用
     */
    @GetMapping(EntrypointUrl.METHOD_SCENE_MANAGE_CONTENT)
    @ApiOperation(value = "压测场景详情")
    public ResponseResult<SceneDetailResponse> getContent(@ApiParam(value = "id") Long id) {
        ResponseResult<SceneManageWrapperResponse> resDTO = getDetailForEdit(id);
        if (!resDTO.getSuccess()) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, resDTO.getError().getMsg());
        }
        return ResponseResult.success(convertSceneManageWrapper2SceneDetail(resDTO.getData()));
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_MANAGE_SEARCH)
    @ApiOperation(value = "压测场景列表")
    public ResponseResult<List<SceneManageListOutput>> getList(
        @ApiParam(name = "current", value = "页码", required = true) Integer pageNumber,
        @ApiParam(name = "pageSize", value = "页大小", required = true) Integer pageSize,
        @ApiParam(name = "customerName", value = "客户名称") String customerName,
        @ApiParam(name = "tenantId", value = "客户ID") Long tenantId,
        @ApiParam(name = "sceneId", value = "压测场景ID") Long sceneId,
        @ApiParam(name = "sceneName", value = "压测场景名称") String sceneName,
        @ApiParam(name = "status", value = "压测状态") Integer status,
        @ApiParam(name = "sceneIds", value = "场景ids，逗号分割") String sceneIds,
        @ApiParam(name = "lastPtStartTime", value = "压测结束时间") String lastPtStartTime,
        @ApiParam(name = "lastPtEndTime", value = "压测结束时间") String lastPtEndTime
    ) {
        /*
         * 1、封装参数
         * 2、调用查询服务
         * 3、返回指定格式
         */
        SceneManageQueryInput queryVO = new SceneManageQueryInput();
        queryVO.setPageSize(pageSize);
        queryVO.setPageNumber(pageNumber);
        queryVO.setTenantId(tenantId);
        queryVO.setSceneId(sceneId);
        queryVO.setSceneName(sceneName);
        queryVO.setStatus(status);
        List<Long> sceneIdList = new ArrayList<>();
        if (StringUtils.isNotBlank(sceneIds)) {
            String[] strList = sceneIds.split(",");
            for (String s : strList) {
                sceneIdList.add(Long.valueOf(s));
            }
        }
        queryVO.setSceneIds(sceneIdList);
        queryVO.setLastPtStartTime(lastPtStartTime);
        queryVO.setLastPtEndTime(lastPtEndTime);
        PageInfo<SceneManageListOutput> pageInfo = sceneManageService.queryPageList(queryVO);
        return ResponseResult.success(pageInfo.getList(), pageInfo.getTotal());
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
        return ResponseResult.success(response);
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
            List<BusinessActivityDetailResponse> activity = Lists.newArrayList();
            wrapperDTO.getBusinessActivityConfig().forEach(data -> {
                BusinessActivityDetailResponse dto = new BusinessActivityDetailResponse();
                dto.setBusinessActivityId(data.getBusinessActivityId());
                dto.setBusinessActivityName(data.getBusinessActivityName());
                dto.setTargetTPS(data.getTargetTPS());
                dto.setTargetRT(data.getTargetRT());
                dto.setTargetSuccessRate(data.getTargetSuccessRate());
                dto.setTargetSA(data.getTargetSA());
                activity.add(dto);
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

    private String buildRule(SceneSlaRefResponse slaRefDTO) {
        SceneSlaRefInput input = new SceneSlaRefInput();
        BeanUtils.copyProperties(slaRefDTO, input);
        Map<String, Object> dataMap = SlaUtil.matchCondition(input, new SendMetricsEvent());
        return String.valueOf(dataMap.get("type"))
            + dataMap.get("compare")
            + slaRefDTO.getRule().getDuring()
            + dataMap.get("unit")
            + "连续出现"
            + slaRefDTO.getRule().getTimes()
            + "次";
    }

    private String convertIdsToNames(String[] ids, List<BusinessActivityDetailResponse> detailList) {
        if (ids == null || ids.length == 0 || CollectionUtils.isEmpty(detailList)) {
            return null;
        }
        Map<String, String> detailMap = ListHelper.transferToMap(detailList,
            data -> String.valueOf(data.getBusinessActivityId()),
            BusinessActivityDetailResponse::getBusinessActivityName);

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

    @ApiOperation(value = "|_ 更改脚本对应的压测场景的文件")
    @PutMapping(EntrypointUrl.METHOD_SCENE_MANAGE_UPDATE_FILE)
    public ResponseResult<?> updateFile(@RequestBody @Validated UpdateSceneFileRequest request) {
        sceneManageService.updateFileByScriptId(request);
        return ResponseResult.success();
    }

    @GetMapping(value = EntrypointUrl.METHOD_SCENE_MANAGE_QUERY_BY_IDS)
    public ResponseResult<List<SceneManageWrapperOutput>> getByIds(@RequestParam("sceneIds") List<Long> sceneIds) {
        List<SceneManageWrapperOutput> byIds = sceneManageService.getByIds(sceneIds);
        return ResponseResult.success(byIds);
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_MANAGE_LIST)
    @ApiOperation(value = "不分页查询所有场景带脚本信息")
    public ResponseResult<List<SceneManageListResp>> getSceneManageList() {

        List<SceneManageListOutput> sceneManageListOutputs = sceneManageService.querySceneManageList();
        // 转换
        List<SceneManageListResp> list = sceneManageListOutputs.stream()
            .map(output -> {
                SceneManageListResp resp = new SceneManageListResp();
                BeanUtils.copyProperties(output, resp);
                return resp;
            }).collect(Collectors.toList());
        return ResponseResult.success(list);
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_MANAGE_CALC_FLOW)
    @ApiOperation(value = "预估流量计算")
    public ResponseResult<BigDecimal> calcFlow(@RequestBody SceneManageWrapperReq wrapperReq) {
        SceneManageWrapperInput input = new SceneManageWrapperInput();
        BeanUtils.copyProperties(wrapperReq, input);
        BigDecimal flow = sceneManageService.calcEstimateFlow(input);
        return ResponseResult.success(flow.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * 获取机器数量范围
     *
     * @param concurrenceNum 并发数量
     * @return -
     */
    @GetMapping(EntrypointUrl.METHOD_SCENE_MANAGE_GET_IP_NUMBER)
    @ApiOperation(value = "")
    public ResponseResult<StrategyResp> getIpNum(Integer concurrenceNum, Integer tpsNum) {
        StrategyOutputExt output = strategyConfigService.getStrategy(concurrenceNum, tpsNum);
        StrategyResp resp = new StrategyResp();
        BeanUtils.copyProperties(output, resp);
        return ResponseResult.success(resp);
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_MANAGE_CHECK_AND_UPDATE_SCRIPT)
    @ApiOperation(value = "解析脚本")
    public ResponseResult<ScriptCheckResp> checkAndUpdate(@RequestBody ScriptCheckAndUpdateReq req) {
        ScriptVerityRespExt scriptVerityRespExt = sceneManageService.checkAndUpdate(req.getRequest(), req.getUploadPath(),
            req.isAbsolutePath(), req.isUpdate());
        return ResponseResult.success(SceneTaskOpenConverter.INSTANCE.ofScriptVerityRespExt(scriptVerityRespExt));
    }
}
