package io.shulie.takin.cloud.app.service.impl;

import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import cn.hutool.http.HttpUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.core.exceptions.ExceptionUtil;

import cn.hutool.http.Method;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.shulie.takin.cloud.app.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.Page;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.data.entity.CallbackEntity;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.app.service.CallbackLogService;
import io.shulie.takin.cloud.data.service.CallbackMapperService;

import javax.annotation.Resource;

/**
 * 回调服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
@Slf4j(topic = "CALLBACK")
public class CallbackServiceImpl implements CallbackService {
    @javax.annotation.Resource
    CallbackLogService callbackLogService;
    @javax.annotation.Resource(name = "callbackMapperServiceImpl")
    CallbackMapperService callbackMapper;

    @Resource
    RedisTemplate<String, Object> stringRedisTemplate;

    /**
     * {@inheritDoc}
     * 最近6个小时之内、非心跳类型（新加逻辑）
     * 且未完成（老逻辑）
     */
    @Override
    public PageInfo<CallbackEntity> list(int pageNumber, int pageSize, boolean isCompleted) {
        try (Page<Object> ignored = PageMethod.startPage(pageNumber, pageSize)) {
            List<CallbackEntity> sourceList = callbackMapper.lambdaQuery()
                .gt(CallbackEntity::getCreateTime, DateUtil.offsetHour(new Date(), -6))
                .eq(CallbackEntity::getCompleted, isCompleted)
                .and(t ->
                    // (阈值时间为空 || 阈值时间小于等于当前时间)
                    t.isNull(CallbackEntity::getThresholdTime)
                        .or(c -> c.le(CallbackEntity::getThresholdTime, new Date())))
                .list();
            return new PageInfo<>(sourceList);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long create(String url, CallbackType type, byte[] content) {
        int typeValue = type == null ? -1 : type.getCode();
        CallbackEntity callbackEntity = new CallbackEntity().setUrl(url).setType(typeValue).setContext(content);
        callbackMapper.save(callbackEntity);
        return callbackEntity.getId();
    }

    /**
     * 给CallbackType RESOURCE_EXAMPLE_HEARTBEAT、PRESSURE_EXAMPLE_HEARTBEAT用
     * @param url
     * @param type
     * @param content
     * @param pressureExampleId
     */
    @Override
    public void create(String url, CallbackType type, String content, Long pressureExampleId) {
        if(pressureExampleId == null) {
            return;
        }
        String redisKey = String.format(RedisKeyUtil.callbackKey, pressureExampleId);
        if(stringRedisTemplate.opsForValue().setIfAbsent(redisKey, "0", 12, TimeUnit.HOURS)) {
            Long callbackId = create(url, type, content.getBytes(StandardCharsets.UTF_8));
            stringRedisTemplate.opsForValue().set(redisKey, callbackId.toString(), 12, TimeUnit.HOURS);
        } else {
            Long callbackId = Long.parseLong(stringRedisTemplate.opsForValue().get(redisKey).toString());
            if(callbackId != null && callbackId > 0) {
                LambdaUpdateWrapper<CallbackEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(CallbackEntity::getCompleted, Boolean.FALSE)
                        .set(CallbackEntity::getThresholdTime, null)
                        .set(CallbackEntity::getCreateTime, new Date())
                        .eq(CallbackEntity::getId, callbackId);
                callbackMapper.update(updateWrapper);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void callback(Long id, String url, Integer type, byte[] content) {
        HashMap<String, Object> query = new HashMap<>(2);
        query.put("id", id);
        query.put("type", type);
        // 组装请求地址
        String requestUrl = HttpUtil.urlWithForm(url, query, StandardCharsets.UTF_8, true);
        // 组装请求
        HttpRequest request = HttpUtil.createRequest(Method.POST, requestUrl)
            .contentType(ContentType.JSON.getValue())
            .setConnectionTimeout(3000)
            .body(content);
        // 记录请求
        Long callbackLogId = callbackLogService.create(id, type, request.getUrl(), content);
        if(callbackLogId == null || callbackLogId == 0) {
            return;
        }
        byte[] result;
        // 接收响应
        try (HttpResponse response = request.execute()) {
            result = response.bodyBytes();
            callbackLogService.fill(callbackLogId, result);
        } catch (RuntimeException e) {
            callbackLogService.fill(callbackLogId, ExceptionUtil.stacktraceToOneLineString(e, 500));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateCompleted(long callbackId, Boolean completed) {
        callbackMapper.lambdaUpdate()
            .set(CallbackEntity::getCompleted, completed)
            .eq(CallbackEntity::getId, callbackId)
            .update();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateThresholdTime(long callbackId) {
        try {
            CallbackEntity callback = callbackMapper.getById(callbackId);
            Long logCount = callbackLogService.count(callbackId);
            DateTime thresholdTime = DateUtil.offsetMillisecond(callback.getCreateTime(), 2 << logCount);
            callbackMapper.lambdaUpdate()
                .set(CallbackEntity::getThresholdTime, thresholdTime)
                .eq(CallbackEntity::getId, callback.getId()).update();
        } catch (Exception e) {
            log.error("更新阈值时间失败:{}\n", callbackId, e);
        }
    }
}
