package io.shulie.takin.cloud.app.controller.notify;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.service.FileExampleService;
import io.shulie.takin.cloud.model.request.file.FailedRequest;
import io.shulie.takin.cloud.model.request.file.ProgressRequest;

/**
 * 文件资源状态上报
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j(topic = "NOTIFY")
@Tag(name = "文件资源任务上报")
@RequestMapping("/notify/job/file")
@RestController("NotiftFileController")
public class FileController {
    @javax.annotation.Resource
    FileExampleService fileExampleService;

    /**
     * 上报文件资源下载调度
     *
     * @param requests 请求
     * @return -
     */
    @PostMapping("progress/update")
    @Operation(summary = "更新进度")
    public ApiResult<Object> updateProgress(@RequestBody List<ProgressRequest> requests) {
        fileExampleService.updateProgress(requests);
        return ApiResult.success();
    }

    /**
     * 上报文件资源下载失败
     *
     * @param requests 请求
     * @return -
     */
    @PostMapping("failed")
    @Operation(summary = "失败")
    public ApiResult<Object> failed(@RequestBody FailedRequest requests) {
        fileExampleService.fail(requests.getId(), requests.getMessage());
        return ApiResult.success();
    }
}
