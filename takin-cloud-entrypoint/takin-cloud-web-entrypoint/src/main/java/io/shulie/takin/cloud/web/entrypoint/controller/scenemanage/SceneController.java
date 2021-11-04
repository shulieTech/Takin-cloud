package io.shulie.takin.cloud.web.entrypoint.controller.scenemanage;

import javax.validation.Valid;
import javax.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.common.constants.APIUrls;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.biz.service.scene.SceneService;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest;
import io.shulie.takin.cloud.open.response.scene.manage.SceneDetailResponse;

/**
 * 场景控制器 - 新
 *
 * @author 张天赐
 */
@RestController("webSceneController")
@Api(tags = "压测场景管理-新")
@RequestMapping(APIUrls.TRO_API_URL + "v2/scene")
public class SceneController {

    @Resource
    SceneService sceneService;

    @PostMapping("/create")
    @ApiOperation(value = "新增压测场景")
    public ResponseResult<Long> create(@RequestBody @Valid WriteSceneRequest request) {
        return ResponseResult.success(sceneService.create(request));
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新压测场景")
    public ResponseResult<Boolean> update(@RequestBody @Valid WriteSceneRequest request) {
        return ResponseResult.success(sceneService.update(request));
    }

    @RequestMapping("/detail")
    @ApiOperation(value = "获取压测场景详情")
    public ResponseResult<SceneDetailResponse> detail(@RequestParam(required = false) Long sceneId) {
        if (sceneId == null) {
            return ResponseResult.fail(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR.getErrorCode(), "场景主键不能为空");
        }
        return ResponseResult.success(sceneService.detail(sceneId));
    }
}
