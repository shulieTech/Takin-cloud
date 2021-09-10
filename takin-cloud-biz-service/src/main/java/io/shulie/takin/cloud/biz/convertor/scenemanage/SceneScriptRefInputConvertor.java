package io.shulie.takin.cloud.biz.convertor.scenemanage;

import com.pamirs.takin.entity.domain.entity.scene.manage.SceneScriptRef;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneScriptRefInput;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author mubai
 * @date 2020-10-30 14:32
 */
@Mapper
public interface SceneScriptRefInputConvertor {
    SceneScriptRefInputConvertor INSTANCE = Mappers.getMapper(SceneScriptRefInputConvertor.class);

    SceneScriptRefInput of(SceneScriptRef scriptRef);

    List<SceneScriptRefInput> ofList(List<SceneScriptRef> scriptRefs);
}
