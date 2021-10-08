package io.shulie.takin.cloud.biz.service.scene.impl;

import javax.annotation.Resource;

import com.pamirs.takin.entity.dao.scene.manage.TSceneScriptRefMapper;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneScriptRef;
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
    private TSceneScriptRefMapper tSceneScriptRefMapper;

    @Override
    public synchronized SceneScriptRef selectByExample(SceneScriptRefQueryParam param) {
        return tSceneScriptRefMapper.selectByExample(param);
    }
}
