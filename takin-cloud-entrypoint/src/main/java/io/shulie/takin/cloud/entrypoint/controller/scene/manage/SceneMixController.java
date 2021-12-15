package io.shulie.takin.cloud.entrypoint.controller.scene.manage;

import javax.validation.Valid;
import javax.annotation.Resource;

import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.biz.service.scene.SceneService;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.biz.service.scene.SceneSynchronizeService;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneRequest;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SynchronizeRequest;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneDetailV2Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 场景控制器 - 新
 *
 * @author 张天赐
 */
@Api(tags = "压测场景-混合压测")
@RestController
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_SCENE_MIX)
public class SceneMixController {

    @Resource
    SceneService sceneService;
    @Resource
    SceneSynchronizeService sceneSynchronizeService;

    @PostMapping(EntrypointUrl.METHOD_SCENE_MIX_CREATE)
    @ApiOperation(value = "新增压测场景")
    public ResponseResult<Long> create(@RequestBody @Valid SceneRequest request) {
        return ResponseResult.success(sceneService.create(request));
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_MIX_UPDATE)
    @ApiOperation(value = "更新压测场景")
    public ResponseResult<Boolean> update(@RequestBody @Valid SceneRequest request) {
        return ResponseResult.success(sceneService.update(request));
    }

    @GetMapping(EntrypointUrl.METHOD_SCENE_MIX_DETAIL)
    @ApiOperation(value = "获取压测场景详情")
    public ResponseResult<SceneDetailV2Response> detail(@RequestParam(required = false) Long sceneId) {
        if (sceneId == null) {
            return ResponseResult.fail(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR.getErrorCode(), "场景主键不能为空");
        }
        return ResponseResult.success(sceneService.detail(sceneId));
    }

    @PostMapping(EntrypointUrl.METHOD_SCENE_MIX_SYNCHRONIZE)
    @ApiOperation(value = "同步场景信息")
    public ResponseResult<String> update(@RequestBody @Valid SynchronizeRequest request) {
        return ResponseResult.success(sceneSynchronizeService.synchronize(request));
    }

}
