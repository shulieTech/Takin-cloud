package io.shulie.takin.cloud.app.controller;

import java.util.HashMap;

import io.shulie.takin.cloud.constant.ApiUrl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public HashMap<String, Object> checkUp() {
        return new HashMap<String, Object>(1) {{
            put("time", System.currentTimeMillis());
        }};
    }
}
