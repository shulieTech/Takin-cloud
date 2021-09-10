package io.shulie.takin.cloud.biz.service.scene;

import com.pamirs.takin.entity.domain.entity.scene.manage.SceneScriptRef;
import com.pamirs.takin.entity.domain.query.SceneScriptRefQueryParam;

/**
 * @author mubai
 * @date 2020-05-12 20:22
 */
public interface SceneScriptRefService {

    SceneScriptRef selectByExample(SceneScriptRefQueryParam param);
}
