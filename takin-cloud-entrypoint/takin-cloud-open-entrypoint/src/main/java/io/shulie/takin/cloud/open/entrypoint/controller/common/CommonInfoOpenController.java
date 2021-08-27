package io.shulie.takin.cloud.open.entrypoint.controller.common;

import io.shulie.takin.cloud.biz.output.common.CommonInfosOutput;
import io.shulie.takin.cloud.biz.service.common.CommonInfoService;
import io.shulie.takin.cloud.common.constants.APIUrls;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公共信息控制器
 *
 * @author lipeng
 * @date 2021-06-24 3:41 下午
 */
@Slf4j
@RestController
@Api(tags = "公共信息管理")
@RequestMapping(APIUrls.TRO_OPEN_API_URL + "common/info")
public class CommonInfoOpenController {

    @Autowired
    private CommonInfoService commonInfoService;

    /**
     * 获取cloud配置信息接口
     *
     * @return
     */
    @ApiOperation(value = "获取cloud配置信息接口")
    @GetMapping("/getCloudConfigurationInfos")
    public ResponseResult<CommonInfosOutput> getCloudConfigurationInfos() {
        return ResponseResult.success(commonInfoService.getCommonConfigurationInfos());
    }
}
