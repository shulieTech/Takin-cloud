package io.shulie.takin.cloud.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.app.model.response.ApiResult;

/**
 * 健康检查接口
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Api(tags = "通用接口")
@RestController
@RequestMapping("/common")
public class CommonController {

    @ApiOperation("健康检查")
    @RequestMapping(value = "health/checkup", method = {RequestMethod.GET})
    public ApiResult checkUp() {
        return ApiResult.success(System.currentTimeMillis());
    }
}
