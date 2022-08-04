package io.shulie.takin.cloud.app.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.app.service.FileService;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.model.request.file.AnnounceRequest;

/**
 * 文件资源
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */

@Slf4j
@RestController
@Tag(name = "文件资源")
@RequestMapping("/file")
public class FileController {
    @javax.annotation.Resource
    FileService fileService;

    @Operation(summary = "下发")
    @PostMapping("announce")
    public ApiResult<Long> announce(
        @Parameter(description = "文件列表", required = true) @RequestBody AnnounceRequest request) {
        return ApiResult.success(fileService.announce(request.getWatchmanIdList(), request.getFileList()));
    }
}
