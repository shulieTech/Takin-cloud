package io.shulie.takin.cloud.data.dao.scenemanage;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pamirs.takin.entity.domain.entity.scenemanage.SceneScriptRef;
import io.shulie.takin.cloud.data.model.mysql.SceneBigFileSliceEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneScriptRefEntity;
import io.shulie.takin.cloud.data.param.scenemanage.SceneBigFileSliceParam;

/**
 * @author moriarty
 */
public interface SceneBigFileSliceDAO extends IService<SceneBigFileSliceEntity> {

    int create(SceneBigFileSliceParam param);

    int isFileSliced(SceneBigFileSliceParam param);

    int update(SceneBigFileSliceParam param);

    SceneScriptRefEntity selectRef(SceneBigFileSliceParam param);

    int updateRef(SceneScriptRefEntity entity);

    boolean isFileNeedSlice(Long refId);

    SceneBigFileSliceEntity selectOne(SceneBigFileSliceParam param);

    Long createRef(SceneScriptRef ref);
}
