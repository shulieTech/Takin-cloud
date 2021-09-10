package io.shulie.takin.cloud.web.entrypoint.controller.api;

import java.util.Map;

import javax.annotation.Resource;

import com.pamirs.takin.entity.dao.report.TReportMapper;
import com.pamirs.takin.entity.dao.scene.manage.TSceneManageMapper;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.common.beans.response.ResponseResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianshui
 * @date 2020/8/30 下午2:08
 */
@RestController
@RequestMapping("/api/noauth")
public class NoAuthController {

    @Resource
    private TSceneManageMapper TSceneManageMapper;

    @Resource
    private TReportMapper TReportMapper;

    /**
     * 恢复压测中的场景状态
     * update t_scene_manage set `status`=0 where id=？;
     * update t_report set `status`=2 where scene_id=？;
     *
     * @param paramMap 参数
     * @return -
     */
    @PutMapping("/resume/scenetask")
    public ResponseResult<String> resumeSceneTask(@RequestBody Map<String, Object> paramMap) {
        if (paramMap == null || paramMap.get("sceneId") == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_RUNNING_PARAM_VERIFY_ERROR, "sceneId cannot be null");
        }
        Long sceneId = Long.parseLong(String.valueOf(paramMap.get("sceneId")));
        TReportMapper.resumeStatus(sceneId);
        TSceneManageMapper.resumeStatus(sceneId);
        return ResponseResult.success("resume success");
    }
}
