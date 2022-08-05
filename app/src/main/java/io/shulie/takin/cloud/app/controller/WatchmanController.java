package io.shulie.takin.cloud.app.controller;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import com.github.pagehelper.PageInfo;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.model.resource.Resource;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.data.entity.WatchmanEntity;
import io.shulie.takin.cloud.app.service.WatchmanService;
import io.shulie.takin.cloud.model.request.watchman.BatchRequest;
import io.shulie.takin.cloud.model.response.watchman.ListResponse;
import io.shulie.takin.cloud.model.response.WatchmanStatusResponse;
import io.shulie.takin.cloud.model.request.watchman.RegisteRequest;
import io.shulie.takin.cloud.model.response.watchman.RegisteResponse;

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

    @Operation(summary = "状态-批量")
    @PostMapping("status/batch")
    public ApiResult<Map<Long, WatchmanStatusResponse>> statusBatch(
        @Parameter(description = "调度器主键集合", required = true) @RequestBody BatchRequest request) {
        Map<Long, WatchmanStatusResponse> result = new HashMap<>(request.getWatchmanIdList().size());
        request.getWatchmanIdList().forEach(t -> result.put(t, watchmanService.status(t)));
        return ApiResult.success(result);
    }

    @Operation(summary = "调度器列表")
    @PostMapping("list")
    public ApiResult<List<ListResponse>> list(
        @Parameter(description = "分页页码", required = true) Integer pageNumber,
        @Parameter(description = "分页容量", required = true) Integer pageSize,
        @Parameter(description = "调度器主键集合", required = true) @RequestBody List<Long> watchmanIdList) {
        PageInfo<WatchmanEntity> list = watchmanService.list(pageNumber, pageSize, watchmanIdList);
        List<ListResponse> result = list.getList().stream()
            .map(t -> {
                Resource resource = new Resource().setCpu(0d).setMemory(0L);
                watchmanService.getResourceList(t.getId()).forEach(c -> {
                    if (c.getCpu() != null) {resource.setCpu(resource.getCpu() + c.getCpu());}
                    if (c.getMemory() != null) {resource.setMemory(resource.getMemory() + c.getMemory());}
                    resource.setName(c.getName());
                    resource.setType(c.getType());
                    resource.setNfsDir(c.getNfsDir());
                    resource.setNfsServer(c.getNfsServer());
                    resource.setNfsTotalSpace(c.getNfsTotalSpace());
                    resource.setNfsUsableSpace(c.getNfsUsableSpace());
                });
                return new ListResponse()
                    .setId(t.getId())
                    .setRef(t.getRef())
                    .setRefSign(t.getRefSign())
                    .setResource(resource);
            }).collect(Collectors.toList());
        return ApiResult.success(result, list.getTotal());
    }

    @Operation(summary = "资源容量列表")
    @GetMapping("resource")
    public ApiResult<List<Resource>> resourceList(@Parameter(description = "调度主键", required = true) @RequestParam Long watchmanId) {
        return ApiResult.success(watchmanService.getResourceList(watchmanId));
    }

    @Operation(summary = "资源容量列表-批量")
    @PostMapping("resource/batch")
    public ApiResult<Map<Long, List<Resource>>> resourceBatchList(
        @Parameter(description = "调度器主键集合", required = true) @RequestBody BatchRequest request) {
        Map<Long, List<Resource>> result = new HashMap<>(request.getWatchmanIdList().size());
        request.getWatchmanIdList().forEach(t -> result.put(t, watchmanService.getResourceList(t)));
        return ApiResult.success(result);
    }

    @Operation(summary = "注册调度机")
    @PostMapping("registe")
    public ApiResult<RegisteResponse> registe(@RequestBody RegisteRequest registeRequest) {
        return ApiResult.success(watchmanService.generate(registeRequest.getHeader(), registeRequest.getBody()));
    }
}
