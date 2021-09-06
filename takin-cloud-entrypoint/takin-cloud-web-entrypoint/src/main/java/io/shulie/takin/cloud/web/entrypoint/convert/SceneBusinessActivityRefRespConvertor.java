package io.shulie.takin.cloud.web.entrypoint.convert;

import io.shulie.takin.cloud.biz.output.scenemanage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.web.entrypoint.response.scenemanage.SceneManageWrapperResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author mubai
 * @date 2020-10-30 12:45
 */
@Mapper
public interface SceneBusinessActivityRefRespConvertor {

    SceneBusinessActivityRefRespConvertor INSTANCE = Mappers.getMapper(SceneBusinessActivityRefRespConvertor.class);

    SceneManageWrapperResponse.SceneBusinessActivityRefResponse of(SceneManageWrapperOutput.SceneBusinessActivityRefOutput output);

    List<SceneManageWrapperResponse.SceneBusinessActivityRefResponse> ofList(List<SceneManageWrapperOutput.SceneBusinessActivityRefOutput> list);

}
