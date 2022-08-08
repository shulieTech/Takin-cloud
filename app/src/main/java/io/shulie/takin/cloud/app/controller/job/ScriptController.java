package io.shulie.takin.cloud.app.controller.job;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.service.ScriptService;
import io.shulie.takin.cloud.model.request.job.script.BuildRequest;
import io.shulie.takin.cloud.model.request.job.script.AnnounceRequest;

/**
 * 脚本校验
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@RestController
@Tag(name = "脚本任务")
@RequestMapping("/job/script")
public class ScriptController {

    @javax.annotation.Resource
    ScriptService scriptService;

    @PostMapping("announce")
    @Operation(summary = "下发脚本校验命令")
    public ApiResult<Long> announce(
        @Parameter(description = "请求参数", required = true) @RequestBody AnnounceRequest request) {
        Long id = scriptService.announce(request.getCallbackUrl(), request.getScriptPath(),
            request.getDataFilePath(), request.getAttachmentsPath(), request.getPluginPath());
        return ApiResult.success(id);
    }

    @Operation(summary = "构建脚本")
    @PostMapping(value = "build")
    public ApiResult<String> buildScript(@Valid @RequestBody BuildRequest request) {
        return ApiResult.success(scriptService.build(request));
    }
}
