package io.shulie.takin.cloud.app.service;

import java.util.List;

import io.shulie.takin.cloud.data.entity.PressureEntity;
import io.shulie.takin.cloud.model.request.job.pressure.StartRequest;
import io.shulie.takin.cloud.model.response.PressureConfig;
import io.shulie.takin.cloud.data.entity.PressureExampleEntity;
import io.shulie.takin.cloud.model.request.job.pressure.ModifyConfig;

/**
 * 施压任务服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface PressureService {

    /**
     * 启动
     *
     * @param info 施压任务信息
     * @return 施压任务主键
     */
    String start(StartRequest info);

    /**
     * 停止
     *
     * @param pressureId 施压任务主键
     */
    void stop(Long pressureId);

    /**
     * 查看配置
     *
     * @param id  施压任务主键
     * @param ref 关键词
     * @return 配置内容
     */
    List<PressureConfig> getConfig(Long id, String ref);

    /**
     * 修改配置
     *
     * @param id      施压任务主键
     * @param context 配置内容
     */
    void modifyConfig(Long id, ModifyConfig context);

    /**
     * 获取数据对象 - 施压任务
     *
     * @param id 数据主键
     * @return Entity
     */
    PressureEntity entity(Long id);

    /**
     * 获取数据对象 - 施压任务实例
     *
     * @param exampleId 数据主键
     * @return Entity
     */
    PressureExampleEntity exampleEntity(Long exampleId);

    /**
     * 获取数据对象 - 施压任务实例
     * <p>根据任务主键查询任务实例列表</p>
     *
     * @param pressureId 施压任务主键
     * @return 施压任务实例列表
     */
    List<PressureExampleEntity> exampleEntityList(Long pressureId);

    /**
     * 启动事件
     *
     * @param pressureId 施压任务主键
     */
    void onStart(Long pressureId);

    /**
     * 停止事件
     *
     * @param pressureId 施压任务主键
     */
    void onStop(Long pressureId);
}
