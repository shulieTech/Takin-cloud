package io.shulie.takin.cloud.web.entrypoint.controller.scenemanage;

import javax.annotation.Resource;
import javax.validation.Valid;

import io.shulie.takin.cloud.biz.service.scene.SceneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.common.constants.APIUrls;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest;

/**
 * 场景控制器 - 新
 *
 * @author 张天赐
 */
@RestController
@Api(tags = "压测场景管理-新")
@RequestMapping(APIUrls.TRO_API_URL + "v2/scene_manage")
public class SceneController {

    @Resource
    SceneService sceneService;

    @PostMapping
    @ApiOperation(value = "新增压测场景")
    public ResponseResult<Long> create(@RequestBody @Valid WriteSceneRequest request) {
        return ResponseResult.success(sceneService.create(request));
    }
}
