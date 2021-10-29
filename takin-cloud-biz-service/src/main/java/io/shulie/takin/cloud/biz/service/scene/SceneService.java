package io.shulie.takin.cloud.biz.service.scene;

import java.util.Map;
import java.util.List;

import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest;
import io.shulie.takin.cloud.open.response.scene.manage.SceneDetailResponse;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.Goal;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.Config;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.Content;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.MonitoringGoal;
import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest.DataValidation;

/**
 * 场景 - 服务
 *
 * @author 张天赐
 */
public interface SceneService {
    /**
     * 创建压测场景
     *
     * @param in 入参
     * @return 场景主键
     */
    Long create(WriteSceneRequest in);

    /**
     * 更新压测场景
     *
     * @param in 入参
     * @return 场景主键
     * <ul>
     *     <li>true :更新</li>
     *     <li>false:未找到业务活动</li>
     *     <li>异常  :运行时异常</li>
     * </ul>
     */
    Boolean update(WriteSceneRequest in);

    /**
     * 获取场景详情
     *
     * @param sceneId 场景主键
     * @return 场景详情
     */
    SceneDetailResponse detail(long sceneId);

    /**
     * 获取场景的基础信息
     *
     * @param sceneId 场景主键
     * @return 基础信息
     */
    Object getBasicInfo(long sceneId);

    /**
     * 获取脚本解析结果
     *
     * @param sceneId 场景主键
     * @return 解析结果
     */
    List<?> getAnalysisResult(long sceneId);

    /**
     * 获取数据验证配置
     *
     * @param sceneId 场景主键
     * @return 数据验证配置
     */
    DataValidation getDataValidation(long sceneId);

    /**
     * 获取压测内容
     *
     * @param sceneId 场景主键
     * @return 压测内容<节点MD5, 压测内容>
     */
    Map<String, Content> getContent(long sceneId);

    /**
     * 获取压测目标
     *
     * @param sceneId 场景主键
     * @return 压测目标<节点MD5, 目标对象>
     */
    Map<String, Goal> getGoal(long sceneId);

    /**
     * 获取压测线程组配置
     *
     * @param sceneId 场景主键
     * @return 线程组配置<节点MD5, 配置对象>
     */
    Config getConfig(long sceneId);

    /**
     * 获取压测SLA
     *
     * @param sceneId 场景主键
     * @return SLA列表
     */
    List<MonitoringGoal> getMonitoringGoal(long sceneId);
}
