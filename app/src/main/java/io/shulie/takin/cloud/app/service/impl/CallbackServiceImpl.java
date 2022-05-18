package io.shulie.takin.cloud.app.service.impl;

import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.Page;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.app.entity.CallbackEntity;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.app.entity.CallbackLogEntity;
import io.shulie.takin.cloud.app.service.mapper.CallbackMapperService;
import io.shulie.takin.cloud.app.service.mapper.CallbackLogMapperService;

/**
 * 回调服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
@Slf4j(topic = "CALLBACK")
public class CallbackServiceImpl implements CallbackService {
    @javax.annotation.Resource
    CallbackMapperService callbackMapperService;
    @javax.annotation.Resource
    CallbackLogMapperService callbackLogMapperService;

    @Override
    public PageInfo<CallbackEntity> list(int pageNumber, int pageSize, boolean isCompleted) {
        try (Page<Object> ignored = PageMethod.startPage(pageNumber, pageSize)) {
            List<CallbackEntity> sourceList = callbackMapperService.lambdaQuery()
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
        callbackMapperService.save(new CallbackEntity().setUrl(url).setContext(content));
    }

    @Override
    public Long createLog(long callbackId, String url, byte[] data) {
        CallbackLogEntity callbackLogEntity = new CallbackLogEntity()
            .setRequestUrl(url)
            .setRequestData(data)
            .setCallbackId(callbackId)
            .setRequestTime(new Date());
        callbackLogMapperService.save(callbackLogEntity);
        return callbackLogEntity.getId();
    }

    @Override
    public boolean fillLog(long callbackLogId, byte[] data) {
        CallbackLogEntity callbackLogEntity = callbackLogMapperService.getById(callbackLogId);
        if (callbackLogEntity == null) {
            log.warn("{}对应的数据库记录未找到", callbackLogId);
            return false;
        } else {
            String successFlag = "{\"error\":null,\"data\":\"SUCCESS\",\"totalNum\":null,\"success\":true}";
            boolean completed = successFlag.equals(StrUtil.utf8Str(data));
            // 填充日志信息
            callbackLogMapperService.updateById(new CallbackLogEntity()
                .setId(callbackLogId)
                .setResponseData(data)
                .setCompleted(completed)
                .setResponseTime(new Date())
            );
            // 更新回调的状态
            if (completed) {
                callbackMapperService.lambdaUpdate().set(CallbackEntity::getCompleted, true)
                    .eq(CallbackEntity::getId, callbackLogEntity.getCallbackId())
                    .update();
            }
            // 更新阈值时间 - 防止回调堆积
            else {updateThresholdTime(callbackLogEntity.getCallbackId());}
            // 返回结果
            return completed;
        }
    }

    /**
     * 更新阈值时间
     *
     * @param callbackId 回调主键
     */
    private void updateThresholdTime(long callbackId) {
        try {
            CallbackEntity callback = callbackMapperService.getById(callbackId);
            Long logCount = callbackLogMapperService.lambdaQuery()
                .eq(CallbackLogEntity::getCallbackId, callbackId)
                .count();
            DateTime thresholdTime = DateUtil.offsetMillisecond(callback.getCreateTime(), 2 << logCount);
            callbackMapperService.lambdaUpdate()
                .set(CallbackEntity::getThresholdTime, thresholdTime)
                .eq(CallbackEntity::getId, callback.getId())
                .update();
        } catch (Exception e) {
            log.error("更新阈值时间失败:{}\n", callbackId, e);
        }
    }
}
