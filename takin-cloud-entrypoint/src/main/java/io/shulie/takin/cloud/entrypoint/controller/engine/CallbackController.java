package io.shulie.takin.cloud.entrypoint.controller.engine;

import javax.annotation.Resource;

import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pamirs.takin.entity.domain.vo.engine.EngineNotifyParam;

import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.biz.service.scene.EngineCallbackService;

/**
 * @author 何仲奇
 * @date 2020/9/23 2:42 下午
 */
@RestController
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_ENGINE_CALLBACK)
public class CallbackController {

    @Resource(type = EngineCallbackService.class)
    EngineCallbackService engineCallbackService;

    @PostMapping(EntrypointUrl.METHOD_ENGINE_CALLBACK_TASK_RESULT_NOTIFY)
    @ApiOperation(value = "引擎回调状态")
    public ResponseResult<?> taskResultNotify(@RequestBody EngineNotifyParam notify) {
        return engineCallbackService.notifyEngineState(notify);
    }
}
