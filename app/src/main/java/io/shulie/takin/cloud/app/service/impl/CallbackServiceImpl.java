package io.shulie.takin.cloud.app.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.Page;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.app.entity.CallbackEntity;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.app.entity.CallbackLogEntity;
import io.shulie.takin.cloud.app.mapper.CallbackLogMapper;
import io.shulie.takin.cloud.app.service.mapper.CallbackMapperService;

/**
 * 回调服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
@Slf4j(topic = "callback")
public class CallbackServiceImpl implements CallbackService {
    @Resource
    CallbackLogMapper callbackLogMapper;
    @Resource
    CallbackMapperService callbackMapperService;

    @Override
    public PageInfo<CallbackEntity> list(int pageNumber, int pageSize, boolean isCompleted) {
        try (Page<Object> ignored = PageHelper.startPage(pageNumber, pageSize)) {
            List<CallbackEntity> sourceList = callbackMapperService.lambdaQuery()
                .eq(CallbackEntity::getCompleted, isCompleted)
                .list();
            return new PageInfo<>(sourceList);
        }
    }

    @Override
    public Long createLog(long callbackId, String url, byte[] data) {
        CallbackLogEntity callbackLogEntity = new CallbackLogEntity() {{
            setRequestUrl(url);
            setRequestData(data);
            setCallbackId(callbackId);
            setRequestTime(new Date());
        }};
        callbackLogMapper.insert(callbackLogEntity);
        return callbackLogEntity.getId();
    }

    @Override
    public void fillLog(long callbackLogId, byte[] data) {
        CallbackLogEntity callbackLogEntity = callbackLogMapper.selectById(callbackLogId);
        if (callbackLogEntity == null) {
            log.warn("{}对应的数据库记录未找到", callbackLogId);
        } else {
            boolean completed = "SUCCESS".equals(StrUtil.utf8Str(data));
            // 填充日志信息
            callbackLogMapper.updateById(new CallbackLogEntity() {{
                setId(callbackLogId);
                setResponseData(data);
                setCompleted(completed);
                setResponseTime(new Date());
            }});
            // 更新回调的状态
            if (completed) {
                callbackMapperService.lambdaUpdate().set(CallbackEntity::getCompleted, true)
                    .eq(CallbackEntity::getId, callbackLogEntity.getCallbackId())
                    .update();
            }
        }
    }
}
