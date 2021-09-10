package io.shulie.takin.cloud.web.entrypoint.convert;

import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.web.entrypoint.response.scenemanage.SceneManageWrapperResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author mubai
 * @date 2020-10-30 12:56
 */

@Mapper
public interface SceneSlaRefRespConvertor {

    SceneSlaRefRespConvertor INSTANCE = Mappers.getMapper(SceneSlaRefRespConvertor.class);

    SceneManageWrapperResponse.SceneSlaRefResponse of(SceneManageWrapperOutput.SceneSlaRefOutput output);

    List<SceneManageWrapperResponse.SceneSlaRefResponse> ofList(List<SceneManageWrapperOutput.SceneSlaRefOutput> output);
}
