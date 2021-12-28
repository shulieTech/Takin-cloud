package io.shulie.takin.cloud.entrypoint.controller.engine;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import com.google.common.collect.Lists;

import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.biz.service.engine.EnginePluginService;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.biz.output.engine.EnginePluginDetailOutput;
import io.shulie.takin.cloud.biz.output.engine.EnginePluginSimpleInfoOutput;
import io.shulie.takin.cloud.sdk.model.request.engine.EnginePluginWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.engine.EnginePluginFetchWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.engine.EnginePluginStatusWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.engine.EnginePluginDetailsWrapperReq;

import lombok.extern.slf4j.Slf4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 引擎控制器
 *
 * @author lipeng
 * @date 2021-01-06 2:53 下午
 */
@Slf4j
@RestController
@Api(tags = "压测引擎管理")
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_ENGINE_PLUGIN)
public class PluginOpenController {

    @Resource(type = EnginePluginService.class)
    EnginePluginService enginePluginService;

    @ApiOperation(value = "获取引擎支持的插件信息")
    @PostMapping(EntrypointUrl.METHOD_ENGINE_PLUGIN_LIST)
    public ResponseResult<Map<String, List<EnginePluginSimpleInfoOutput>>> fetchAvailableEnginePlugins(@RequestBody EnginePluginFetchWrapperReq request) {
        List<String> pluginTypes = request.getPluginTypes();
        List<String> pluginTypesInput = Lists.newArrayList();
        //插件类型小写存储
        if (CollectionUtils.isNotEmpty(pluginTypes)) {
            pluginTypes.forEach(item -> pluginTypesInput.add(StringUtils.lowerCase(item)));
        } else {
            throw new TakinCloudException(TakinCloudExceptionEnum.ENGINE_PLUGIN_PARAM_VERIFY_ERROR, "参数不能为空");
        }
        return ResponseResult.success(enginePluginService.findEngineAvailablePluginsByType(pluginTypesInput));
    }

    @ApiOperation(value = "获取引擎插件详情信息")
    @PostMapping(EntrypointUrl.METHOD_ENGINE_PLUGIN_DETAILS)
    public ResponseResult<EnginePluginDetailOutput> fetchEnginePluginDetails(@RequestBody EnginePluginDetailsWrapperReq request) {
        Long pluginId = request.getPluginId();
        if (pluginId == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.ENGINE_PLUGIN_PARAM_VERIFY_ERROR, "pluginId不能为空");
        }
        return enginePluginService.findEnginePluginDetails(pluginId);
    }

    @ApiOperation(value = "保存引擎插件")
    @PostMapping(EntrypointUrl.METHOD_ENGINE_PLUGIN_SAVE)
    public ResponseResult<?> saveEnginePlugin(@RequestBody EnginePluginWrapperReq request) {
        enginePluginService.saveEnginePlugin(request);
        return ResponseResult.success();
    }

    @ApiOperation("启用引擎插件")
    @PostMapping(EntrypointUrl.METHOD_ENGINE_PLUGIN_ENABLE)
    public ResponseResult<?> enableEnginePlugin(@RequestBody EnginePluginStatusWrapperReq request) {
        Long pluginId = request.getPluginId();
        if (Objects.isNull(pluginId) || pluginId == 0) {
            throw new TakinCloudException(TakinCloudExceptionEnum.ENGINE_PLUGIN_PARAM_VERIFY_ERROR, "pluginId不能为空");
        }
        enginePluginService.changeEnginePluginStatus(pluginId, true);
        return ResponseResult.success();
    }

    @ApiOperation("禁用引擎插件")
    @PostMapping(EntrypointUrl.METHOD_ENGINE_PLUGIN_DISABLE)
    public ResponseResult<?> disableEnginePlugin(@RequestBody EnginePluginStatusWrapperReq request) {
        Long pluginId = request.getPluginId();
        if (Objects.isNull(pluginId) || pluginId == 0) {
            throw new TakinCloudException(TakinCloudExceptionEnum.ENGINE_PLUGIN_PARAM_VERIFY_ERROR, "pluginId不能为空");
        }
        enginePluginService.changeEnginePluginStatus(pluginId, false);
        return ResponseResult.success();
    }

}
