package io.shulie.takin.cloud.entrypoint.controller.engine;

import com.pamirs.takin.entity.domain.vo.engine.EngineNotifyParam;
import io.shulie.takin.cloud.biz.service.scene.EngineCallbackService;
import io.shulie.takin.cloud.common.constants.ApiUrls;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 何仲奇
 * @date 2020/9/23 2:42 下午
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "engine/callback")
public class EngineCallbackController {

    @Autowired
    private EngineCallbackService engineCallbackService;

    @PostMapping()
    @ApiOperation(value = "引擎回调状态")
    public ResponseResult<?> taskResultNotify(@RequestBody EngineNotifyParam notify) {
        return engineCallbackService.notifyEngineState(notify);
    }
}
