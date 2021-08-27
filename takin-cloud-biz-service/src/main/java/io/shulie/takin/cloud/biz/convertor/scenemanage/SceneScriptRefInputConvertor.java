package io.shulie.takin.cloud.biz.convertor.scenemanage;

import com.pamirs.takin.entity.domain.entity.scenemanage.SceneScriptRef;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneScriptRefInput;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Author: mubai
 * @Date: 2020-10-30 14:32
 * @Description:
 */
@Mapper
public interface SceneScriptRefInputConvertor {
    SceneScriptRefInputConvertor INSTANCE = Mappers.getMapper(SceneScriptRefInputConvertor.class);

    SceneScriptRefInput of(SceneScriptRef scriptRef);

    List<SceneScriptRefInput> ofList(List<SceneScriptRef> scriptRefs);
}
