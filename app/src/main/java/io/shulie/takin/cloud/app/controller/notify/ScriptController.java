package io.shulie.takin.cloud.app.controller.notify;

import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.service.ScriptService;
import io.shulie.takin.cloud.model.request.job.script.ReportRequest;

/**
 * 脚本校验上报
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j(topic = "NOTIFY")
@Tag(name = "脚本校验上报")
@RestController("NotiftScriptController")
@RequestMapping("/notify/job/script")
public class ScriptController {

    @javax.annotation.Resource
    ScriptService scriptService;

    @PostMapping("verification/report")
    @Operation(summary = "上报")
    public ApiResult<Object> report(
        @Parameter(description = "请求参数", required = true) @RequestBody ReportRequest request) {
        scriptService.report(request.getId(), request.getCompleted(), request.getMessage());
        return ApiResult.success();
    }
}
