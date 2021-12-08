package io.shulie.takin.cloud.entrypoint.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.common.beans.response.ResponseResult;

/**
 * 健康状态检测接口
 *
 * @author 张天赐
 */
@RestController
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_HEALTH)
public class HealthController {

    /**
     * 检测应用状态
     */
    @ApiOperation(value = "检测应用状态")
    @RequestMapping(EntrypointUrl.METHOD_HEALTH_CHECK)
    public ResponseResult<Long> check() {
        return ResponseResult.success(System.currentTimeMillis());
    }
}
