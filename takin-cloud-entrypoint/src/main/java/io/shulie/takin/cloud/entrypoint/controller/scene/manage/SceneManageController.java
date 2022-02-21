package io.shulie.takin.cloud.entrypoint.controller.scene.manage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.validation.Valid;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageQueryInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageWrapperInput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageListOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.biz.service.strategy.StrategyConfigService;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.CloudUpdateSceneFileRequest;
import io.shulie.takin.cloud.entrypoint.convert.SceneBusinessActivityRefInputConvert;
import io.shulie.takin.cloud.entrypoint.convert.SceneScriptRefInputConvert;
import io.shulie.takin.cloud.entrypoint.convert.SceneSlaRefInputConverter;
import io.shulie.takin.cloud.entrypoint.convert.SceneTaskOpenConverter;
import io.shulie.takin.cloud.ext.api.AssetExtApi;
import io.shulie.takin.cloud.ext.content.asset.AssetBillExt;
import io.shulie.takin.cloud.ext.content.enginecall.StrategyOutputExt;
import io.shulie.takin.cloud.ext.content.response.Response;
import io.shulie.takin.cloud.ext.content.script.ScriptVerityRespExt;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageDeleteReq;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.ScriptCheckAndUpdateReq;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageListResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.ScriptCheckResp;
import io.shulie.takin.cloud.sdk.model.response.strategy.StrategyResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
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
    @Resource
    private PluginManager pluginManager;
    @Resource(type = SceneManageService.class)
    private SceneManageService sceneManageService;
    @Resource(type = StrategyConfigService.class)
    private StrategyConfigService strategyConfigService;

    @ApiOperation(value = "新增压测场景")
    @PostMapping(value = EntrypointUrl.METHOD_SCENE_MANAGE_SAVE)
    public ResponseResult<Long> add(@RequestBody @Valid SceneManageWrapperReq wrapperReq) {
        SceneManageWrapperInput input = new SceneManageWrapperInput();
        dataModelConvert(wrapperReq, input);
        Long aLong = sceneManageService.addSceneManage(input);
        return ResponseResult.success(aLong);
    }

    public void dataModelConvert(SceneManageWrapperReq wrapperReq, SceneManageWrapperInput input) {
        BeanUtil.copyProperties(wrapperReq, input);
        input.setStopCondition(SceneSlaRefInputConverter.ofList(wrapperReq.getStopCondition()));
        input.setWarningCondition(SceneSlaRefInputConverter.ofList(wrapperReq.getWarningCondition()));
        input.setBusinessActivityConfig(
            SceneBusinessActivityRefInputConvert.ofLists(wrapperReq.getBusinessActivityConfig()));
        input.setUploadFile(SceneScriptRefInputConvert.ofList(wrapperReq.getUploadFile()));

    }

    @PutMapping(value = EntrypointUrl.METHOD_SCENE_MANAGE_UPDATE)
    @ApiOperation(value = "修改压测场景")
    public ResponseResult<String> update(@RequestBody @Valid SceneManageWrapperReq wrapperReq) {
        SceneManageWrapperInput input = new SceneManageWrapperInput();
        dataModelConvert(wrapperReq, input);
        sceneManageService.updateSceneManage(input);
        return ResponseResult.success();
    }

    @DeleteMapping(value = EntrypointUrl.METHOD_SCENE_MANAGE_DELETE)
    @ApiOperation(value = "删除压测场景")
    public ResponseResult<String> delete(@RequestBody SceneManageDeleteReq deleteReq) {
        sceneManageService.delete(deleteReq.getId());
        return ResponseResult.success("删除成功");
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_MANAGE_SEARCH)
    @ApiOperation(value = "压测场景列表")
    public ResponseResult<List<SceneManageListResp>> getList(
        @ApiParam(name = "current", value = "页码", required = true) Integer pageNumber,
        @ApiParam(name = "pageSize", value = "页大小", required = true) Integer pageSize,
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
        queryVO.setPageNumber(pageNumber);
        queryVO.setPageSize(pageSize);
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
        // 转换
        List<SceneManageListResp> list = pageInfo.getList().stream()
            .map(output -> {
                SceneManageListResp resp = BeanUtil.copyProperties(output, SceneManageListResp.class);
                resp.setHasAnalysisResult(StrUtil.isNotBlank(output.getScriptAnalysisResult()));
                return resp;
            }).collect(Collectors.toList());
        return ResponseResult.success(list, pageInfo.getTotal());
    }

    @ApiOperation(value = "|_ 更改脚本对应的压测场景的文件")
    @PutMapping(EntrypointUrl.METHOD_SCENE_MANAGE_UPDATE_FILE)
    public ResponseResult<?> updateFile(@RequestBody @Validated CloudUpdateSceneFileRequest request) {
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
            .map(t -> BeanUtil.copyProperties(t, SceneManageListResp.class))
            .collect(Collectors.toList());
        return ResponseResult.success(list);
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_MANAGE_CALC_FLOW)
    @ApiOperation(value = "预估流量计算")
    public ResponseResult<BigDecimal> calcFlow(@RequestBody AssetBillExt bill) {
        BigDecimal flow = new BigDecimal(0);
        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (assetExtApi != null) {
            Response<BigDecimal> calcResponse = assetExtApi.calcEstimateAmount(bill);
            if (calcResponse.isSuccess()) {
                flow = calcResponse.getData();
            }
        }
        return ResponseResult.success(flow);
    }

    /**
     * 获取机器数量范围
     *
     * @param concurrenceNum 并发数量
     * @return -
     */
    @GetMapping(EntrypointUrl.METHOD_SCENE_MANAGE_GET_IP_NUMBER)
    @ApiOperation(value = "获取机器数量范围")
    public ResponseResult<StrategyResp> getIpNum(Integer concurrenceNum, Integer tpsNum) {
        StrategyOutputExt output = strategyConfigService.getStrategy(concurrenceNum, tpsNum);
        StrategyResp resp = BeanUtil.copyProperties(output, StrategyResp.class);
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
