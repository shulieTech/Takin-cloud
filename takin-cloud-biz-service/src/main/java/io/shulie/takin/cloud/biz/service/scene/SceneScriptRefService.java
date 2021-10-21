package io.shulie.takin.cloud.biz.service.scene;

import com.pamirs.takin.entity.domain.entity.scene.manage.SceneScriptRef;
import com.pamirs.takin.entity.domain.query.SceneScriptRefQueryParam;

/**
 * @author mubai
 * @date 2020-05-12 20:22
 */
public interface SceneScriptRefService {
    /**
     * 依据条件查询
     *
     * @param param 查询条件
     * @return 结果数据
     */
    SceneScriptRef selectByExample(SceneScriptRefQueryParam param);
}
