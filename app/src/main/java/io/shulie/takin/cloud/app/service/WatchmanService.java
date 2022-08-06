package io.shulie.takin.cloud.app.service;

import java.util.List;

import com.github.pagehelper.PageInfo;

import io.shulie.takin.cloud.model.resource.Resource;
import io.shulie.takin.cloud.data.entity.WatchmanEntity;
import io.shulie.takin.cloud.model.resource.ResourceSource;
import io.shulie.takin.cloud.model.response.WatchmanStatusResponse;

import io.shulie.takin.cloud.model.watchman.Register.Body;
import io.shulie.takin.cloud.model.watchman.Register.Header;
import io.shulie.takin.cloud.model.response.watchman.RegisteResponse;

/**
 * 调度服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface WatchmanService {
    /**
     * 调度机列表
     *
     * @param pageNumber     分页页码
     * @param pageSize       分页容量
     * @param watchmanIdList 调度机主键集合
     * @return 分页列表
     */
    PageInfo<WatchmanEntity> list(int pageNumber, int pageSize, List<Long> watchmanIdList);

    /**
     * 获取调度所属的资源列表
     *
     * @param watchmanId 调度主键
     * @return 资源列表
     */
    List<Resource> getResourceList(Long watchmanId);

    /**
     * 获取调度器所属的资源总量
     *
     * @param watchmanId 调度主键
     * @return 资源总量(列表汇总结果)
     */
    Resource getResourceSum(Long watchmanId);

    /**
     * 调度注册
     *
     * @param ref     关键词
     * @param refSign 关键词签名
     * @return true/false
     */
    boolean register(String ref, String refSign);

    /**
     * 根据refSign获取调度信息
     *
     * @param refSign {@link WatchmanEntity#getRef}
     * @return 调度信息 | null
     */
    WatchmanEntity ofRefSign(String refSign);

    /**
     * 获取调度机状态
     *
     * @param watchmanId 调度主键
     * @return 状态
     */
    WatchmanStatusResponse status(Long watchmanId);

    /**
     * 资源上报
     *
     * @param watchmanId 调度主键
     * @param context    上报内容
     */
    void upload(long watchmanId, List<ResourceSource> context);

    /**
     * 心跳
     *
     * @param watchmanId 调度器主键
     */
    void onHeartbeat(long watchmanId);

    /**
     * 恢复正常事件
     *
     * @param watchmanId 调度主键
     */
    void onNormal(long watchmanId);

    /**
     * 异常事件
     *
     * @param watchmanId 调度主键
     * @param message    异常内容
     */
    void onAbnormal(long watchmanId, String message);

    /**
     * 生成调度机信息
     *
     * @param header 头部信息
     * @param body   主要信息
     * @return 调度机信息
     */
    RegisteResponse generate(Header header, Body body);
}
