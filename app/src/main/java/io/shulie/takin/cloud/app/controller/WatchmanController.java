package io.shulie.takin.cloud.app.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import com.github.pagehelper.PageInfo;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.model.resource.Resource;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.app.entity.WatchmanEntity;
import io.shulie.takin.cloud.app.service.WatchmanService;
import io.shulie.takin.cloud.model.response.WatchmanStatusResponse;

/**
 * 资源
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@RestController
@Tag(name = "调度器")
@RequestMapping("/watchman")
public class WatchmanController {
    @javax.annotation.Resource
    WatchmanService watchmanService;

    @Operation(summary = "状态")
    @GetMapping("status")
    public ApiResult<WatchmanStatusResponse> status(@Parameter(description = "调度主键", required = true) Long watchmanId) {
        return ApiResult.success(watchmanService.status(watchmanId));
    }

    @Operation(summary = "调度器列表")
    @GetMapping("list")
    public ApiResult<List<WatchmanEntity>> list(
        @Parameter(description = "分页页码", required = true) Integer pageNumber,
        @Parameter(description = "分页容量", required = true) Integer pageSize) {
        PageInfo<WatchmanEntity> list = watchmanService.list(pageNumber, pageSize);
        return ApiResult.success(list.getList(), list.getTotal());
    }

    @Operation(summary = "资源容量列表")
    @GetMapping("resource")
    public ApiResult<List<Resource>> resourceList(@Parameter(description = "调度主键", required = true) Long watchmanId) {
        return ApiResult.success(watchmanService.getResourceList(watchmanId));
    }

}
