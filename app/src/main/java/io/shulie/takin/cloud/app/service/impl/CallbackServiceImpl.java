package io.shulie.takin.cloud.app.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.nio.charset.StandardCharsets;

import cn.hutool.http.HttpUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONException;

import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.Page;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.data.entity.CallbackEntity;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.data.entity.CallbackLogEntity;
import io.shulie.takin.cloud.data.service.CallbackMapperService;
import io.shulie.takin.cloud.data.service.CallbackLogMapperService;

/**
 * 回调服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
@Slf4j(topic = "CALLBACK")
public class CallbackServiceImpl implements CallbackService {
    @javax.annotation.Resource(name = "callbackMapperServiceImpl")
    CallbackMapperService callbackMapper;
    @javax.annotation.Resource(name = "callbackLogMapperServiceImpl")
    CallbackLogMapperService callbackLogMapper;

    private static final String RES_SUCCESS_TAG = "SUCCESS";

    @Override
    public PageInfo<CallbackEntity> list(int pageNumber, int pageSize, boolean isCompleted) {
        try (Page<Object> ignored = PageMethod.startPage(pageNumber, pageSize)) {
            List<CallbackEntity> sourceList = callbackMapper.lambdaQuery()
                // 未完成
                .eq(CallbackEntity::getCompleted, isCompleted)
                // 并且
                .and(t ->
                    // (阈值时间为空 || 阈值时间小于等于当前时间)
                    t.isNull(CallbackEntity::getThresholdTime)
                        .or(c -> c.le(CallbackEntity::getThresholdTime, new Date())))
                .list();
            return new PageInfo<>(sourceList);
        }
    }

    @Override
    public void create(String url, byte[] content) {
        callbackMapper.save(new CallbackEntity().setUrl(url).setContext(content));
    }

    @Override
    public Long createLog(long callbackId, String url, byte[] data) {
        CallbackLogEntity callbackLogEntity = new CallbackLogEntity()
            .setRequestUrl(url)
            .setRequestData(data)
            .setCallbackId(callbackId)
            .setRequestTime(new Date());
        callbackLogMapper.save(callbackLogEntity);
        return callbackLogEntity.getId();
    }

    @Override
    public boolean fillLog(long callbackLogId, byte[] data) {
        CallbackLogEntity callbackLogEntity = callbackLogMapper.getById(callbackLogId);
        if (callbackLogEntity == null) {
            log.warn("{}对应的数据库记录未找到", callbackLogId);
            return false;
        } else {
            String response = StrUtil.utf8Str(data);
            boolean completed = false;
            try {
                JSONObject resJson = JSON.parseObject(response);
                if (Objects.nonNull(resJson) && Boolean.TRUE.equals(resJson.getBoolean("success"))
                    && Objects.equals(resJson.getString("data"), RES_SUCCESS_TAG)) {
                    completed = true;
                }
            } catch (JSONException e) {
                log.error("CallbackServiceImpl#fillLog", e);
            }
            // 填充日志信息
            callbackLogMapper.updateById(new CallbackLogEntity()
                .setId(callbackLogId)
                .setResponseData(data)
                .setCompleted(completed)
                .setResponseTime(new Date())
            );
            // 更新回调的状态
            if (completed) {
                callbackMapper.lambdaUpdate().set(CallbackEntity::getCompleted, true)
                    .eq(CallbackEntity::getId, callbackLogEntity.getCallbackId())
                    .update();
            }
            // 更新阈值时间 - 防止回调堆积
            else {
                updateThresholdTime(callbackLogEntity.getCallbackId());
            }

            // 返回结果
            return completed;
        }
    }

    @Override
    public boolean callback(Long id, String callbackUrl, String content) {
        // 组装请求
        HttpRequest request = HttpUtil.createPost(callbackUrl);
        request.contentType(ContentType.JSON.getValue());
        request.setConnectionTimeout(3000).body(content);
        byte[] responseData;
        // 接收相应
        try (HttpResponse response = request.execute()) {
            responseData = response.bodyBytes();
        }
        boolean completed = isSuccess(responseData);
        //修改回调记录
        if (Objects.nonNull(id)) {
            CallbackEntity entity = callbackMapper.getById(id);
            entity.setCompleted(completed);
            entity.setThresholdTime(new Date());
            callbackMapper.updateById(entity);
        }
        //创建回调记录
        else {
            CallbackEntity callbackEntity = new CallbackEntity()
                .setCompleted(completed)
                .setUrl(callbackUrl)
                .setContext(content.getBytes(StandardCharsets.UTF_8))
                .setThresholdTime(new Date());
            callbackMapper.save(callbackEntity);
        }
        return completed;
    }

    private boolean isSuccess(byte[] responseData) {
        String response = StrUtil.utf8Str(responseData);
        boolean completed = false;
        try {
            JSONObject resJson = JSON.parseObject(response);
            if (Objects.nonNull(resJson) && Boolean.TRUE.equals(resJson.getBoolean("success"))
                && Objects.equals(resJson.getString("data"), RES_SUCCESS_TAG)) {
                completed = true;
            }
        } catch (JSONException e) {
            log.error("CallbackServiceImpl#isSuccess", e);
        }
        return completed;
    }

    /**
     * 更新阈值时间
     *
     * @param callbackId 回调主键
     */
    private void updateThresholdTime(long callbackId) {
        try {
            CallbackEntity callback = callbackMapper.getById(callbackId);
            Long logCount = callbackLogMapper.lambdaQuery()
                .eq(CallbackLogEntity::getCallbackId, callbackId)
                .count();
            DateTime thresholdTime = DateUtil.offsetMillisecond(callback.getCreateTime(), 2 << logCount);
            callbackMapper.lambdaUpdate()
                .set(CallbackEntity::getThresholdTime, thresholdTime)
                .eq(CallbackEntity::getId, callback.getId())
                .update();
        } catch (Exception e) {
            log.error("更新阈值时间失败:{}\n", callbackId, e);
        }
    }

}
