package io.shulie.takin.cloud.app.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import com.github.pagehelper.PageInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.app.entity.WatchmanEntity;
import io.shulie.takin.cloud.app.model.resource.Resource;
import io.shulie.takin.cloud.app.service.WatchmanService;
import io.shulie.takin.cloud.app.model.response.ApiResult;

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

    @Operation(summary = "列表")
    @RequestMapping(value = "list", method = {RequestMethod.GET})
    public ApiResult<List<WatchmanEntity>> list(
        @Parameter(description = "分页页码", required = true) Integer pageNumber,
        @Parameter(description = "分页容量", required = true) Integer pageSize) {
        PageInfo<WatchmanEntity> list = watchmanService.list(pageNumber, pageSize);
        return ApiResult.success(list.getList(), list.getTotal());
    }

    @Operation(summary = "资源列表")
    @RequestMapping(value = "resource", method = {RequestMethod.GET})
    public ApiResult<List<Resource>> resourceList(@Parameter(description = "调度主键", required = true) Long watchmanId) throws JsonProcessingException {
        return ApiResult.success(watchmanService.getResourceList(watchmanId));
    }

    @Operation(summary = "注册")
    @RequestMapping(value = "register", method = {RequestMethod.GET})
    public ApiResult<Boolean> register(
        @Parameter(description = "关键词") String ref,
        @Parameter(description = "关键词签名") String refSign) {
        return ApiResult.success(watchmanService.register(ref, refSign));
    }

}
