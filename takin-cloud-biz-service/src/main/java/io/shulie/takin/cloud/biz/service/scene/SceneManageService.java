package io.shulie.takin.cloud.biz.service.scene;

import java.math.BigDecimal;
import java.util.List;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageStartRecordVO;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageQueryInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageWrapperInput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageListOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneBusinessActivityRefOutput;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOpitons;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.request.scenemanage.UpdateSceneFileRequest;
import io.shulie.takin.ext.content.asset.AssetBillExt;
import io.shulie.takin.ext.content.script.ScriptVerityRespExt;

/**
 * @author qianshui
 * @date 2020/4/17 下午3:31
 */
public interface SceneManageService {

    /**
     * 新增场景
     *
     * @param wrapperVO 包装参数
     * @return -
     */
    Long addSceneManage(SceneManageWrapperInput wrapperVO);

    PageInfo<SceneManageListOutput> queryPageList(SceneManageQueryInput queryVO);

    void updateSceneManage(SceneManageWrapperInput wrapperVO);

    void updateSceneManageStatus(UpdateStatusBean statusVO);

    void delete(Long id);

    SceneManageWrapperOutput getSceneManage(Long id, SceneManageQueryOpitons options);

    /**
     * 根据场景ID获取业务活动配置
     *
     * @param sceneId 场景主键
     * @return -
     */
    List<SceneBusinessActivityRefOutput> getBusinessActivityBySceneId(Long sceneId);

    /**
     * 预估流量计算
     */
    BigDecimal calcEstimateFlow(List<AssetBillExt> bills);

    /**
     * 获取压测场景目标路径,当前以/结尾
     *
     * @param sceneId 场景主键
     * @return -
     */
    String getDestPath(Long sceneId);

    /**
     * 严格更新 压测场景生命周期
     *
     * @param statusVO 状态参数
     */
    Boolean updateSceneLifeCycle(UpdateStatusBean statusVO);

    /**
     * 记录场景启动过程  比如job 是否创建成功，压力节点 是否创建成功，
     *
     * @param recordVO 记录参数
     */
    void reportRecord(SceneManageStartRecordVO recordVO);

    /**
     * 不分页查询所有场景信息，带脚本信息
     *
     * @return -
     */
    List<SceneManageListOutput> querySceneManageList();

    /**
     * 更新 脚本id 关联的场景下的文件
     *
     * @param request 请求所需的参数
     */
    void updateFileByScriptId(UpdateSceneFileRequest request);

    List<SceneManageWrapperOutput> getByIds(List<Long> sceneIds);

    void saveUnUploadLogInfo();

    ScriptVerityRespExt checkAndUpdate(List<String> request, String uploadPath, boolean isAbsolutePath, boolean update);
}
