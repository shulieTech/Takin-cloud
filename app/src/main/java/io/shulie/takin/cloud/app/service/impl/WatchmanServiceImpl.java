package io.shulie.takin.cloud.app.service.impl;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.Page;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.digest.HMac;
import com.github.pagehelper.PageInfo;
import cn.hutool.core.text.CharSequenceUtil;
import com.github.pagehelper.page.PageMethod;
import org.springframework.stereotype.Service;
import io.shulie.takin.cloud.constant.Message;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.shulie.takin.cloud.app.util.ResourceUtil;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.model.resource.Resource;
import io.shulie.takin.cloud.model.watchman.Register;
import io.shulie.takin.cloud.data.entity.WatchmanEntity;
import io.shulie.takin.cloud.app.service.WatchmanService;
import io.shulie.takin.cloud.model.watchman.Register.Body;
import io.shulie.takin.cloud.model.resource.ResourceSource;
import io.shulie.takin.cloud.model.watchman.Register.Header;
import io.shulie.takin.cloud.constant.enums.NotifyEventType;
import io.shulie.takin.cloud.data.mapper.WatchmanEventMapper;
import io.shulie.takin.cloud.data.entity.WatchmanEventEntity;
import io.shulie.takin.cloud.model.response.WatchmanStatusResponse;
import io.shulie.takin.cloud.data.service.WatchmanMapperService;

/**
 * 调度服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@Service
public class WatchmanServiceImpl implements WatchmanService {
    @javax.annotation.Resource
    JsonService jsonService;
    @javax.annotation.Resource
    WatchmanEventMapper watchmanEventMapper;
    @javax.annotation.Resource
    WatchmanMapperService watchmanMapperService;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageInfo<WatchmanEntity> list(int pageNumber, int pageSize) {
        try (Page<?> ignored = PageMethod.startPage(pageNumber, pageSize)) {
            return new PageInfo<>(watchmanMapperService.list());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Resource> getResourceList(Long watchmanId) {
        List<Resource> result = new ArrayList<>(0);
        // 找到最后一次上报的数据
        try (Page<Resource> ignored = PageMethod.startPage(1, 1)) {
            // 查询条件 - 资源类型的上报
            Wrapper<WatchmanEventEntity> wrapper = new LambdaQueryWrapper<WatchmanEventEntity>()
                .orderByDesc(WatchmanEventEntity::getTime)
                .eq(WatchmanEventEntity::getType, NotifyEventType.WATCHMAN_UPLOAD.getCode())
                .eq(WatchmanEventEntity::getWatchmanId, watchmanId);
            // 执行SQL
            PageInfo<WatchmanEventEntity> watchmanEventList = new PageInfo<>(watchmanEventMapper.selectList(wrapper));
            // 组装数据
            if (!watchmanEventList.getList().isEmpty()) {
                // 组装返回数据
                String eventContextString = watchmanEventList.getList().get(0).getContext();
                Map<String, Object> eventContext = jsonService.readValue(eventContextString, new TypeReference<Map<String, Object>>() {});
                long resourceTime = Long.parseLong(String.valueOf(eventContext.get("time")));
                // TODO 要校验时效
                if (resourceTime < 0) {
                    log.warn("调度资源获取:最后一次上报的资源时效了");
                }
                Object resourceListObject = eventContext.get("data");
                if (resourceListObject instanceof List) {
                    result.addAll(jsonService.readValue(jsonService.writeValueAsString(resourceListObject), new TypeReference<List<Resource>>() {}));
                } else {
                    result.addAll(jsonService.readValue(resourceListObject.toString(), new TypeReference<List<Resource>>() {}));
                }
            }
        } catch (RuntimeException e) {
            log.warn("调度资源获取:JSON解析失败");
            throw e;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean register(String ref, String refSign) {
        // 已存在返回TRUE
        if (ofRefSign(refSign) != null) {return true;}
        return watchmanMapperService.save(new WatchmanEntity().setRef(ref).setRefSign(refSign));
    }

    @Override
    public WatchmanEntity ofRefSign(String refSign) {
        // TODO 签名校验
        WatchmanEntity entity = watchmanMapperService.lambdaQuery().eq(WatchmanEntity::getRefSign, refSign).one();
        if (entity == null) {throw new IllegalArgumentException(CharSequenceUtil.format(Message.WATCHMAN_MISS, refSign));}
        return entity;
    }

    @Override
    public WatchmanStatusResponse status(Long watchmanId) {
        // 是否有(异常/恢复)事件
        WatchmanEventEntity status = lastStatusEvent();
        if (status != null && NotifyEventType.WATCHMAN_ABNORMAL.getCode().equals(status.getType())) {
            Map<String, Object> eventContext = jsonService.readValue(status.getContext(), new TypeReference<Map<String, Object>>() {});
            String message = eventContext.get(Message.MESSAGE_NAME) == null ? null : eventContext.get(Message.MESSAGE_NAME).toString();
            return new WatchmanStatusResponse(status.getTime().getTime(), message);
        }
        // 返回心跳时间
        WatchmanEventEntity heartbeat = lastHeartbeatEvent();
        if (heartbeat != null) {
            return new WatchmanStatusResponse(heartbeat.getTime().getTime(), null);
        }
        return null;
    }

    /**
     * 返回最后一此上报的"状态类型"的事件
     * <ul>
     *     <li>调度异常</li>
     *     <li>调度正常</li>
     * </ul>
     *
     * @return 事件实体
     */
    private WatchmanEventEntity lastStatusEvent() {
        Wrapper<WatchmanEventEntity> statusWrapper = new LambdaQueryWrapper<WatchmanEventEntity>()
            .orderByDesc(WatchmanEventEntity::getTime)
            .in(WatchmanEventEntity::getType, NotifyEventType.WATCHMAN_NORMAL.getCode(), NotifyEventType.WATCHMAN_ABNORMAL.getCode());
        try (Page<?> ignore = PageMethod.startPage(1, 1)) {
            return watchmanEventMapper.selectList(statusWrapper).stream().findFirst().orElse(null);
        }
    }

    /**
     * 返回最后一此上报的"心跳"的事件
     *
     * @return 事件实体
     */
    private WatchmanEventEntity lastHeartbeatEvent() {
        Wrapper<WatchmanEventEntity> heartbeatWrapper = new LambdaQueryWrapper<WatchmanEventEntity>()
            .orderByDesc(WatchmanEventEntity::getTime)
            .eq(WatchmanEventEntity::getType, NotifyEventType.WATCHMAN_HEARTBEAT.getCode());
        try (Page<?> ignore = PageMethod.startPage(1, 1)) {
            return watchmanEventMapper.selectList(heartbeatWrapper).stream().findFirst().orElse(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upload(long watchmanId, List<ResourceSource> content) {
        // resource -> 转换
        List<String> errorMessage = new ArrayList<>(2);
        List<Resource> resourceList = content.stream().map(t -> {
            Double cpu = ResourceUtil.convertCpu(t.getCpu());
            Long memory = ResourceUtil.convertMemory(t.getMemory());
            if (cpu == null) {errorMessage.add(CharSequenceUtil.format(Message.CAN_NOT_CONVERT_CPU, t.getCpu()));}
            if (memory == null) {errorMessage.add(CharSequenceUtil.format(Message.CAN_NOT_CONVERT_MEMORY, t.getMemory()));}
            return new Resource().setCpu(cpu).setMemory(memory);
        }).collect(Collectors.toList());
        // 转换校验
        if (!errorMessage.isEmpty()) {throw new IllegalArgumentException(String.join(Message.COMMA, errorMessage));}
        // 组装入库数据
        Map<String, Object> context = new HashMap<>(2);
        context.put("time", String.valueOf(System.currentTimeMillis()));
        context.put("data", resourceList);
        // 插入数据库
        watchmanEventMapper.insert(new WatchmanEventEntity()
            .setWatchmanId(watchmanId)
            .setType(NotifyEventType.WATCHMAN_UPLOAD.getCode())
            .setContext(jsonService.writeValueAsString(context))
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onHeartbeat(long watchmanId) {
        watchmanEventMapper.insert(new WatchmanEventEntity()
            .setContext("{}").setWatchmanId(watchmanId)
            .setType(NotifyEventType.WATCHMAN_HEARTBEAT.getCode())
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNormal(long watchmanId) {
        watchmanEventMapper.insert(new WatchmanEventEntity()
            .setContext("{}").setWatchmanId(watchmanId)
            .setType(NotifyEventType.WATCHMAN_NORMAL.getCode())
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAbnormal(long watchmanId, String message) {
        ObjectNode content = JsonNodeFactory.instance.objectNode();
        content.put("message", message);
        watchmanEventMapper.insert(new WatchmanEventEntity()
            .setWatchmanId(watchmanId).setContext(content.toPrettyString())
            .setType(NotifyEventType.WATCHMAN_ABNORMAL.getCode())
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Register generate(Header header, Body body) {
        header.setAlg("HS256");
        header.setSign("MD5");
        body.setRef("tianci");
        body.setTimeOfValidity(253402271999999L);
        body.setTimeOfCreate(System.currentTimeMillis());
        String headerString = jsonService.writeValueAsString(header);
        String bodyString = jsonService.writeValueAsString(body);

        String base64HeaderString = Base64.encodeUrlSafe(headerString);
        String base64BodyString = Base64.encodeUrlSafe(bodyString);
        String secret = "shulie@2022";
        log.info("head(base64) " + base64HeaderString);
        log.info("body(base64)" + base64BodyString);
        log.info("secret " + secret);
        HMac hMac = SecureUtil.hmacSha256(secret);
        String verifySignature = hMac.digestBase64(CharSequenceUtil.format("{}.{}", base64HeaderString, base64BodyString), true);
        String ref = CharSequenceUtil.format("{}.{}.{}", base64HeaderString, base64BodyString, verifySignature);
        String refSign = SecureUtil.md5(ref);

        return new Register().setRefSign(refSign).setHeader(header).setBody(body).setRef(ref);
    }

}
