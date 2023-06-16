package io.shulie.takin.cloud.entrypoint.scene.manage;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.*;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageListResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.ScriptCheckResp;
import io.shulie.takin.cloud.sdk.model.response.strategy.StrategyResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import org.springframework.validation.annotation.Validated;

/**
 * @author 何仲奇
 * @date 2020/10/20 3:42 下午
 */
@Validated
@SuppressWarnings("unused")
public interface SceneManageApi {

    /**
     * 根据脚本实例id, 更新所有的场景对应该脚本的文件
     *
     * @param updateSceneFileRequest 请求入参
     */
    void updateSceneFileByScriptId(@Valid CloudUpdateSceneFileRequest updateSceneFileRequest);

    /**
     * 保存
     *
     * @param sceneManageWrapperReq -
     * @return -
     */
    Long saveScene(SceneManageWrapperReq sceneManageWrapperReq);

    /**
     * 更新
     *
     * @param sceneManageWrapperReq -
     * @return -
     */
    String updateScene(SceneManageWrapperReq sceneManageWrapperReq);

    /**
     * 删除
     *
     * @param sceneManageDeleteReq -
     * @return -
     */
    String deleteScene(SceneManageDeleteReq sceneManageDeleteReq);

    /**
     * 获取场景明细 供编辑使用
     *
     * @param sceneManageIdVO -
     * @return -
     */
    SceneManageWrapperResp getSceneDetail(SceneManageIdReq sceneManageIdVO);

    /**
     * 获取场景明细 无租户版本
     *
     * @param sceneManageIdVO -
     * @return 返回值
     */
    SceneManageWrapperResp getSceneDetailNoAuth(SceneManageIdReq sceneManageIdVO);

    /**
     * 不分页查询所有场景带脚本信息
     *
     * @param request -
     * @return -
     */
    List<SceneManageListResp> getSceneManageList(ContextExt request);

    /**
     * 获取压测场景列表
     *
     * @param sceneManageQueryReq -
     * @return 包含total
     */
    ResponseResult<List<SceneManageListResp>> getSceneList(SceneManageQueryReq sceneManageQueryReq);

    /**
     * 流量计算
     *
     * @param sceneManageWrapperReq -
     * @return -
     */
    BigDecimal calcFlow(SceneManageWrapperReq sceneManageWrapperReq);

    /**
     * 获取机器数量范围
     *
     * @param sceneIpNumReq -
     * @return -
     */
    StrategyResp getIpNum(SceneIpNumReq sceneIpNumReq);

    /**
     * 校验并更新脚本
     *
     * @param scriptCheckAndUpdateReq -
     * @return 返回解析失败信息，如果为空，表示解析成功
     */
    ScriptCheckResp checkAndUpdateScript(ScriptCheckAndUpdateReq scriptCheckAndUpdateReq);

    /**
     * 根据主键查询
     *
     * @param req 入参(主键)
     * @return 查询列表
     */
    List<SceneManageWrapperResp> queryByIds(SceneManageQueryByIdsReq req);

    /**
     * 获取压测中场景列表
     *
     * @param queryReq 查询条件
     * @return List<SceneManageRunningResp>
     */
    List<SceneManageRunningResp> getSceneManageRunningList(SceneManageQueryReq queryReq);

}
