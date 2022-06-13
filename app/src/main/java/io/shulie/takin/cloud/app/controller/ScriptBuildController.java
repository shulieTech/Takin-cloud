package io.shulie.takin.cloud.app.controller;

import io.shulie.takin.cloud.app.service.ScriptService;
import io.shulie.takin.cloud.model.request.ScriptBuildRequest;
import io.shulie.takin.cloud.model.request.ScriptCheckRequest;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * ClassName:    BuildScriptController
 * Package:    io.shulie.takin.cloud.app.controller
 * Description:
 * Datetime:    2022/5/24   17:44
 * Author:   chenhongqiao@shulie.com
 */
@Tag(name = "脚本")
@RestController
@Validated
@RequestMapping("/script")
public class ScriptBuildController {

    @Resource
    private ScriptService scriptService;

    @Operation(summary = "构建脚本")
    @PostMapping(value = "build")
    public ApiResult<String> buildScript(@Valid @RequestBody ScriptBuildRequest request) {
        return ApiResult.success(scriptService.buildJmeterScript(request));
    }

    @Operation(summary = "检测脚本")
    @PostMapping(value = "check")
    public ApiResult<Object> checkScript(@Valid @RequestBody ScriptCheckRequest request) {
        return scriptService.checkJmeterScript(request);
    }
}
