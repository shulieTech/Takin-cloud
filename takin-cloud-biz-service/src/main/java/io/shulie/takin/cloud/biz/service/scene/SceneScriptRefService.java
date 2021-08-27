package io.shulie.takin.cloud.biz.service.scene;

import com.pamirs.takin.entity.domain.entity.scenemanage.SceneScriptRef;
import com.pamirs.takin.entity.domain.query.SceneScriptRefQueryParam;

/**
 * @Author: mubai
 * @Date: 2020-05-12 20:22
 * @Description:
 */
public interface SceneScriptRefService {

    SceneScriptRef selectByExample(SceneScriptRefQueryParam param);
}
