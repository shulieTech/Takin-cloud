package io.shulie.takin.cloud.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.model.response.ApiResult;

/**
 * 健康检查接口
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Tag(name = "通用接口")
@RestController
@RequestMapping("/common")
public class CommonController {

    @Operation(summary = "健康检查")
    @RequestMapping(value = "health/checkup", method = {RequestMethod.GET})
    public ApiResult<Long> checkUp() {
        return ApiResult.success(System.currentTimeMillis());
    }
}
