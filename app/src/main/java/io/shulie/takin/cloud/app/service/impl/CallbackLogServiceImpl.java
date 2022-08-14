package io.shulie.takin.cloud.app.service.impl;

import java.util.Date;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONException;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import io.shulie.takin.cloud.constant.Message;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.data.entity.CallbackLogEntity;
import io.shulie.takin.cloud.app.service.CallbackLogService;
import io.shulie.takin.cloud.data.service.CallbackLogMapperService;

/**
 * 回调日志服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
@Slf4j(topic = "CALLBACK")
public class CallbackLogServiceImpl implements CallbackLogService {

    @Lazy
    @javax.annotation.Resource
    CallbackService callbackService;

    @javax.annotation.Resource(name = "callbackLogMapperServiceImpl")
    CallbackLogMapperService callbackLogMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long count(long callbackId) {
        return callbackLogMapper.lambdaQuery()
            .eq(CallbackLogEntity::getCallbackId, callbackId)
            .count();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long create(long callbackId, Integer type, String url, byte[] data) {
        CallbackLogEntity callbackLogEntity = new CallbackLogEntity()
            .setType(type)
            .setRequestUrl(url)
            .setRequestData(data)
            .setCallbackId(callbackId)
            .setRequestTime(new Date());
        callbackLogMapper.save(callbackLogEntity);
        return callbackLogEntity.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fill(long callbackLogId, byte[] data) {
        CallbackLogEntity callbackLogEntity = callbackLogMapper.getById(callbackLogId);
        if (callbackLogEntity == null) {
            log.warn("{}对应的数据库记录未找到", callbackLogId);
        } else {
            boolean completed = isSuccess(data);
            // 填充日志信息
            boolean updateResult = callbackLogMapper.
                lambdaUpdate()
                .eq(CallbackLogEntity::getId, callbackLogId)
                .set(CallbackLogEntity::getResponseData, data)
                .set(CallbackLogEntity::getResponseTime, new Date())
                .set(CallbackLogEntity::getCompleted, completed).update();
            // 更新回调的状态
            if (completed && updateResult) {
                callbackService.updateCompleted(callbackLogEntity.getCallbackId(), true);
            }
            // 更新阈值时间 - 防止回调堆积
            else {
                callbackService.updateThresholdTime(callbackLogEntity.getCallbackId());
            }
        }
    }

    /**
     * 回调的响应判断回调是否成功
     */
    private boolean isSuccess(byte[] responseData) {
        String response = StrUtil.utf8Str(responseData);
        try {
            JSONObject resJson = JSON.parseObject(response);
            return Objects.nonNull(resJson)
                && Boolean.TRUE.equals(resJson.getBoolean(Message.SUCCESS));
        } catch (JSONException e) {
            log.error("CallbackServiceImpl#isSuccess", e);
            return false;
        }
    }
}
