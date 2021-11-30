package io.shulie.takin.cloud.biz.convertor.scenemanage;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.scene.manage.SceneScriptRef;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneScriptRefInput;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author mubai
 * @date 2020-10-30 14:32
 */
@Mapper
public interface SceneScriptRefInputConvertor {
    SceneScriptRefInputConvertor INSTANCE = Mappers.getMapper(SceneScriptRefInputConvertor.class);

    /**
     * 数据转换
     *
     * @param scriptRef 原数据
     * @return 转换后数据
     */
    SceneScriptRefInput of(SceneScriptRef scriptRef);

    /**
     * 数据转换(批量)
     *
     * @param scriptRefs 原数据(批量)
     * @return 转换后数据(批量)
     */
    List<SceneScriptRefInput> ofList(List<SceneScriptRef> scriptRefs);
}
