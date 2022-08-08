package io.shulie.takin.cloud.app.controller.notify;

import java.util.Map;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.shulie.takin.cloud.constant.Message;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.model.callback.FileUsage;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.data.entity.PressureEntity;
import io.shulie.takin.cloud.app.service.PressureService;
import io.shulie.takin.cloud.app.service.CallbackService;

/**
 * 压测使用量
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@Tag(name = "压测使用量")
@RequestMapping("notify/job/pressure/usage")
@RestController("NotiftPressureUsageController")
public class PressureUsageController {
    @javax.annotation.Resource
    JsonService jsonService;
    @javax.annotation.Resource
    PressureService pressureService;
    @javax.annotation.Resource
    CallbackService callbackService;

    /**
     * 上报文件用量
     *
     * @param body 请求体
     * @return -
     */
    @PostMapping("upload/file")
    @Operation(summary = "上报文件用量")
    public ApiResult<Object> register(@RequestBody byte[] body) {
        Map<String, Object> request = jsonService.readValue(new String(body, StandardCharsets.UTF_8), new TypeReference<Map<String, Object>>() {});
        // 从请求体获取到任务主键
        Object taskIdValue = request.get("taskId");
        if (taskIdValue == null) {return ApiResult.fail(Message.UNKNOWN + Message.COMMA + Message.TASK_ID);}
        // 根据任务主键获取任务信息
        long pressureId = Long.parseLong(taskIdValue.toString());
        PressureEntity pressureEntity = pressureService.entity(pressureId);
        if (pressureEntity == null) {return ApiResult.fail(CharSequenceUtil.format(Message.MISS_PRESSURE, pressureId));}
        // 组装回调
        FileUsage content = new FileUsage();
        content.setData(request);
        callbackService.create(pressureEntity.getCallbackUrl(), jsonService.writeValueAsString(content));
        return ApiResult.success();
    }
}
