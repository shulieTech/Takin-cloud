package io.shulie.takin.cloud.open.entrypoint.controller.engine;

import com.google.common.collect.Lists;
import io.shulie.takin.cloud.biz.input.engine.EnginePluginWrapperInput;
import io.shulie.takin.cloud.biz.output.engine.EnginePluginDetailOutput;
import io.shulie.takin.cloud.biz.output.engine.EnginePluginSimpleInfoOutput;
import io.shulie.takin.cloud.biz.service.engine.EnginePluginService;
import io.shulie.takin.cloud.common.constants.ApiUrls;
import io.shulie.takin.cloud.common.constants.EnginePluginConstants;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.open.req.engine.EnginePluginDetailsWrapperReq;
import io.shulie.takin.cloud.open.req.engine.EnginePluginFetchWrapperReq;
import io.shulie.takin.cloud.open.req.engine.EnginePluginStatusWrapperReq;
import io.shulie.takin.cloud.open.req.engine.EnginePluginWrapperReq;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 引擎控制器
 *
 * @author lipeng
 * @date 2021-01-06 2:53 下午
 */
@Slf4j
@RestController
@Api(tags = "压测引擎管理")
@RequestMapping(ApiUrls.TRO_OPEN_API_URL + "engine")
public class EnginePluginOpenController {

    @Resource
    private EnginePluginService enginePluginService;

    @ApiOperation(value = "获取引擎支持的插件信息")
    @PostMapping("/fetchAvailableEnginePlugins")
    public ResponseResult<Map<String, List<EnginePluginSimpleInfoOutput>>> fetchAvailableEnginePlugins(@RequestBody EnginePluginFetchWrapperReq request) {
        List<String> pluginTypes = request.getPluginTypes();
        List<String> pluginTypesInput = Lists.newArrayList();
        //插件类型小写存储
        if(CollectionUtils.isNotEmpty(pluginTypes)) {
            pluginTypes.forEach(item -> pluginTypesInput.add(StringUtils.lowerCase(item)));
        } else{
            throw new TakinCloudException(TakinCloudExceptionEnum.ENGINE_PLUGIN_PARAM_VERIFY_ERROR,"参数不能为空");
        }
        return ResponseResult.success(enginePluginService.findEngineAvailablePluginsByType(pluginTypesInput));
    }

    @ApiOperation(value = "获取引擎插件详情信息")
    @PostMapping("/fetchEnginePluginDetails")
    public ResponseResult<EnginePluginDetailOutput> fetchEnginePluginDetails(@RequestBody EnginePluginDetailsWrapperReq request) {
        Long pluginId = request.getPluginId();
        if(pluginId == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.ENGINE_PLUGIN_PARAM_VERIFY_ERROR,"pluginId不能为空");
        }
        return enginePluginService.findEnginePluginDetails(pluginId);
    }

    @ApiOperation(value = "保存引擎插件")
    @PostMapping("/saveEnginePlugin")
    public ResponseResult saveEnginePlugin(@RequestBody EnginePluginWrapperReq request) {
        EnginePluginWrapperInput input = new EnginePluginWrapperInput();
        BeanUtils.copyProperties(request, input);
        enginePluginService.saveEnginePlugin(input);
        return ResponseResult.success();
    }

    @ApiOperation("启用引擎插件")
    @PostMapping("enableEnginePlugin")
    public ResponseResult enableEnginePlugin(@RequestBody EnginePluginStatusWrapperReq request) {
        Long pluginId = request.getPluginId();
        if(Objects.isNull(pluginId) || pluginId == 0) {
            throw new TakinCloudException(TakinCloudExceptionEnum.ENGINE_PLUGIN_PARAM_VERIFY_ERROR,"pluginId不能为空");
        }
        enginePluginService.changeEnginePluginStatus(pluginId, EnginePluginConstants.ENGINE_PLUGIN_STATUS_ENABLED);
        return ResponseResult.success();
    }

    @ApiOperation("禁用引擎插件")
    @PostMapping("disableEnginePlugin")
    public ResponseResult disableEnginePlugin(@RequestBody EnginePluginStatusWrapperReq request) {
        Long pluginId = request.getPluginId();
        if(Objects.isNull(pluginId) || pluginId == 0) {
            throw new TakinCloudException(TakinCloudExceptionEnum.ENGINE_PLUGIN_PARAM_VERIFY_ERROR,"pluginId不能为空");
        }
        enginePluginService.changeEnginePluginStatus(pluginId, EnginePluginConstants.ENGINE_PLUGIN_STATUS_DISABLED);
        return ResponseResult.success();
    }

}
