package io.shulie.takin.cloud.web.entrypoint.controller.dictionary;

import io.shulie.takin.cloud.biz.cache.DictionaryCache;
import io.shulie.takin.cloud.common.constants.APIUrls;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 何仲奇
 * @Package io.shulie.takin.cloud.controller.dictionary
 * @date 2020/10/19 11:07 上午
 */
@RestController
@RequestMapping(APIUrls.TRO_API_URL)
public class DictionaryController {
    @Autowired
    private DictionaryCache dictionaryCache;

    @GetMapping("/link/dictionary")
    @ApiOperation(value = "全局字典")
    public ResponseResult dictionary() {
        return ResponseResult.success(dictionaryCache.getDicMap());
    }
}
