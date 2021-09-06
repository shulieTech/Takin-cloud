package io.shulie.takin.cloud.biz.service.scene.impl;

import javax.annotation.Resource;

import com.pamirs.takin.entity.dao.scenemanage.TSceneScriptRefMapper;
import com.pamirs.takin.entity.domain.entity.scenemanage.SceneScriptRef;
import com.pamirs.takin.entity.domain.query.SceneScriptRefQueryParam;
import org.springframework.stereotype.Service;
import io.shulie.takin.cloud.biz.service.scene.SceneScriptRefService;

/**
 * @author mubai
 * @date 2020-05-12 20:23
 */

@Service
public class SceneScriptRefServiceImpl implements SceneScriptRefService {
    @Resource
    private TSceneScriptRefMapper TSceneScriptRefMapper;

    @Override
    public synchronized SceneScriptRef selectByExample(SceneScriptRefQueryParam param) {
        return TSceneScriptRefMapper.selectByExample(param);
    }
}
