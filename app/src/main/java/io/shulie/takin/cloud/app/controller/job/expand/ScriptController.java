package io.shulie.takin.cloud.app.controller.job.expand;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.service.ScriptService;
import io.shulie.takin.cloud.model.request.job.script.BuildRequest;

/**
 * 脚本任务拓展
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Tag(name = "脚本任务拓展")
@RequestMapping("/expand/job/script")
@RestController("JobExpandScriptController")
public class ScriptController {
    @javax.annotation.Resource
    ScriptService scriptService;

    @Operation(summary = "构建脚本")
    @PostMapping(value = "build")
    public ApiResult<String> buildScript(
        @Parameter(description = "请求参数", required = true) @Validated @RequestBody BuildRequest request) {
        return ApiResult.success(scriptService.build(request));
    }
}