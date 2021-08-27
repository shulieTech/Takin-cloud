package io.shulie.takin.cloud.web.entrypoint.convert;

import io.shulie.takin.cloud.biz.output.scenemanage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.web.entrypoint.response.scenemanage.SceneManageWrapperResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Author: mubai
 * @Date: 2020-10-30 12:54
 * @Description:
 */

@Mapper
public interface SceneScriptRefRespConvertor {

    SceneScriptRefRespConvertor INSTANCE = Mappers.getMapper(SceneScriptRefRespConvertor.class);

    SceneManageWrapperResponse.SceneScriptRefResponse of(SceneManageWrapperOutput.SceneScriptRefOutput output);

    List<SceneManageWrapperResponse.SceneScriptRefResponse> ofList(List<SceneManageWrapperOutput.SceneScriptRefOutput> output);


}
