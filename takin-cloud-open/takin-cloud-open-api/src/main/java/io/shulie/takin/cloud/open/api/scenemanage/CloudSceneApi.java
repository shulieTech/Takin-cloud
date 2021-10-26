package io.shulie.takin.cloud.open.api.scenemanage;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.validation.groups.Default;

import io.shulie.takin.cloud.open.req.scenemanage.*;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageListResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.cloud.open.resp.scenemanage.ScriptCheckResp;
import io.shulie.takin.cloud.open.resp.strategy.StrategyResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.ext.content.script.ScriptNode;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import org.springframework.validation.annotation.Validated;

/**
 * @author 何仲奇
 * @date 2020/10/20 3:42 下午
 */
@Validated
public interface CloudSceneApi {

    /**
     * 根据脚本实例id, 更新所有的场景对应该脚本的文件
     *
     * @param updateSceneFileRequest 请求入参
     * @return 是否成功结果
     */
    Object updateSceneFileByScriptId(@Valid CloudUpdateSceneFileRequest updateSceneFileRequest);

    /**
     * 保存
     *
     * @param sceneManageWrapperReq -
     * @return -
     */
    ResponseResult<Long> saveScene(SceneManageWrapperReq sceneManageWrapperReq);

    /**
     * 更新
     *
     * @param sceneManageWrapperReq -
     * @return -
     */
    ResponseResult<String> updateScene(SceneManageWrapperReq sceneManageWrapperReq);

    /**
     * 删除
     *
     * @param sceneManageDeleteReq -
     * @return -
     */
    ResponseResult<String> deleteScene(SceneManageDeleteReq sceneManageDeleteReq);

    /**
     * 获取场景明细 供编辑使用
     *
     * @param sceneManageIdVO -
     * @return -
     */
    ResponseResult<SceneManageWrapperResp> getSceneDetail(SceneManageIdReq sceneManageIdVO);

    /**
     * 不分页查询所有场景带脚本信息
     *
     * @param request -
     * @return -
     */
    ResponseResult<List<SceneManageListResp>> getSceneManageList(CloudUserCommonRequestExt request);

    /**
     * 获取压测场景列表
     *
     * @param sceneManageQueryReq -
     * @return -
     */
    ResponseResult<List<SceneManageListResp>> getSceneList(SceneManageQueryReq sceneManageQueryReq);

    /**
     * 流量计算
     *
     * @param sceneManageWrapperReq -
     * @return -
     */
    ResponseResult<BigDecimal> calcFlow(SceneManageWrapperReq sceneManageWrapperReq);

    /**
     * 获取机器数量范围
     *
     * @param sceneIpNumReq -
     * @return -
     */
    ResponseResult<StrategyResp> getIpNum(SceneIpNumReq sceneIpNumReq);

    /**
     * 校验并更新脚本
     *
     * @param scriptCheckAndUpdateReq -
     * @return 返回解析失败信息，如果为空，表示解析成功
     */
    ResponseResult<ScriptCheckResp> checkAndUpdateScript(ScriptCheckAndUpdateReq scriptCheckAndUpdateReq);

    ResponseResult<List<SceneManageWrapperResp>> queryByIds(SceneManageQueryByIdsReq req);

    /**
     * 新版业务流程脚本解析
     */
    ResponseResult<List<ScriptNode>> scriptAnalyze(ScriptAnalyzeRequest request);

}
