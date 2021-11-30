package io.shulie.takin.cloud.entrypoint.controller.scene.manage;

import javax.annotation.Resource;
import javax.validation.Valid;

import io.shulie.takin.cloud.biz.service.scene.SceneService;
import io.shulie.takin.cloud.biz.service.scene.SceneSynchronizeService;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneDetailV2Response;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneRequest;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SynchronizeRequest;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 场景控制器 - 新
 *
 * @author 张天赐
 */
@RestController("webSceneController")
@Api(tags = "压测场景管理-新")
@RequestMapping("v2/scene")
public class SceneController {

    @Resource
    SceneService sceneService;
    @Resource
    SceneSynchronizeService sceneSynchronizeService;

    @PostMapping("/create")
    @ApiOperation(value = "新增压测场景")
    public ResponseResult<Long> create(@RequestBody @Valid SceneRequest request) {
        return ResponseResult.success(sceneService.create(request));
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新压测场景")
    public ResponseResult<Boolean> update(@RequestBody @Valid SceneRequest request) {
        return ResponseResult.success(sceneService.update(request));
    }

    @GetMapping("/detail")
    @ApiOperation(value = "获取压测场景详情")
    public ResponseResult<SceneDetailV2Response> detail(@RequestParam(required = false) Long sceneId) {
        if (sceneId == null) {
            return ResponseResult.fail(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR.getErrorCode(), "场景主键不能为空");
        }
        return ResponseResult.success(sceneService.detail(sceneId));
    }

    @PostMapping("/synchronize")
    @ApiOperation(value = "同步场景信息")
    public ResponseResult<String> update(@RequestBody @Valid SynchronizeRequest request) {
        return ResponseResult.success(sceneSynchronizeService.synchronize(request));
    }

}
