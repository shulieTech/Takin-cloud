package io.shulie.takin.cloud.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.service.ScriptService;
import io.shulie.takin.cloud.model.request.job.script.AnnounceRequest;

/**
 * 脚本校验
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@RestController
@Tag(name = "脚本校验任务")
@RequestMapping("/job/script/verification")
public class ScriptController {

    @javax.annotation.Resource
    ScriptService scriptService;

    @PostMapping("announce")
    @Operation(summary = "下发")
    public ApiResult<Long> announce(
        @Parameter(description = "请求参数", required = true) @RequestBody AnnounceRequest request) {
        Long jobId = scriptService.announce(request.getScriptPath(), request.getDataFilePath(), request.getAttachmentsPath());
        return ApiResult.success(jobId);
    }
}
