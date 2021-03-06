package io.shulie.takin.cloud.entrypoint.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResponse;

/**
 * @author mubai
 * @date 2020-10-30 12:54
 */

@Mapper
public interface SceneScriptRefRespConvertor {

    SceneScriptRefRespConvertor INSTANCE = Mappers.getMapper(SceneScriptRefRespConvertor.class);

    /**
     * 数据转换
     *
     * @param output 原数据
     * @return 转换后数据
     */
    SceneManageWrapperResponse.SceneScriptRefResponse of(SceneManageWrapperOutput.SceneScriptRefOutput output);

    /**
     * 数据转换(批量)
     *
     * @param output 原数据
     * @return 转换后数据
     */
    List<SceneManageWrapperResponse.SceneScriptRefResponse> ofList(List<SceneManageWrapperOutput.SceneScriptRefOutput> output);

}
