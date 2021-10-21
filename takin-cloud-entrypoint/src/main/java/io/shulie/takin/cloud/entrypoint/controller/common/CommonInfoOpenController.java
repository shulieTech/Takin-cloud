package io.shulie.takin.cloud.entrypoint.controller.common;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import io.shulie.takin.cloud.biz.cache.DictionaryCache;
import io.shulie.takin.cloud.biz.output.common.CommonInfosOutput;
import io.shulie.takin.cloud.biz.service.common.CommonInfoService;
import io.shulie.takin.cloud.common.constants.ApiUrls;
import io.shulie.takin.cloud.common.enums.machine.EnumResult;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
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
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_COMMON)
public class CommonInfoOpenController {

    @Resource(type = DictionaryCache.class)
    DictionaryCache dictionaryCache;
    @Resource(type = CommonInfoService.class)
    CommonInfoService commonInfoService;

    /**
     * 获取cloud配置信息接口
     *
     * @return -
     */
    @ApiOperation(value = "获取cloud配置信息接口")
    @GetMapping(EntrypointUrl.METHOD_COMMON_CONFIG)
    public ResponseResult<CommonInfosOutput> getCloudConfigurationInfos() {
        return ResponseResult.success(commonInfoService.getCommonConfigurationInfos());
    }

    @GetMapping(EntrypointUrl.METHOD_COMMON_DICTIONARY)
    @ApiOperation(value = "全局字典")
    public ResponseResult<Map<String, List<EnumResult>>> dictionary() {
        return ResponseResult.success(dictionaryCache.getDicMap());
    }
}
